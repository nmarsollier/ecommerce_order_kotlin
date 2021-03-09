package model.batch

import model.orders.events.repository.EventRepository
import model.orders.events.repository.EventType
import model.orders.projections.order.OrderService
import model.orders.projections.order.repository.Status
import model.orders.projections.orderStatus.repository.OrderStatusRepository
import org.bson.types.ObjectId
import rabbit.EmitArticleValidation
import java.util.concurrent.atomic.AtomicBoolean

class BatchService private constructor(
    private val placeOrdersRunning: AtomicBoolean = AtomicBoolean(),
    private val validatedOrdersRunning: AtomicBoolean = AtomicBoolean(),
    private val orderService: OrderService = OrderService.instance(),
    private val statusRepository: OrderStatusRepository = OrderStatusRepository.instance(),
    private val eventRepository: EventRepository = EventRepository.instance()
) {

    fun processPlacedOrders() {
        Thread {
            if (placeOrdersRunning.compareAndSet(false, true)) {
                statusRepository.findByStatus(Status.PLACED).forEach { stat ->
                    orderService.buildOrder(stat.id)?.let { order ->
                        // Si status de order sigue en PLACED es porque no se validó completamente
                        if (order.status === Status.PLACED) {
                            validateOrder(order.id!!)
                        } else {
                            // En este caso OrderStatus esta desactualizado
                            statusRepository.save(stat.update(order))
                        }
                    }
                }
                placeOrdersRunning.set(false)
            }
        }.start()
    }

    fun processValidatedOrders() {
        Thread {
            if (validatedOrdersRunning.compareAndSet(false, true)) {
                statusRepository.findByStatus(Status.VALIDATED).forEach { stat ->
                    orderService.buildOrder(stat.id)?.let {
                        statusRepository.save(stat.update(it))
                    }
                }
                validatedOrdersRunning.set(false)
            }
        }.start()
    }

    fun processPaymentDefinedOrders() {
        Thread {
            if (validatedOrdersRunning.compareAndSet(false, true)) {
                statusRepository.findByStatus(Status.PAYMENT_DEFINED).forEach { stat ->
                    orderService.buildOrder(stat.id)?.let { order ->
                        statusRepository.save(stat.update(order))
                    }
                }
                validatedOrdersRunning.set(false)
            }
        }.start()
    }

    private fun validateOrder(orderId: ObjectId) {

        val event = eventRepository.findPlaceByOrderId(orderId)
        if (event != null) {
            /**
             * Busca todos los artículos de un evento, los envía a rabbit para que catalog valide si están activos
             */
            Thread {
                if (event.type === EventType.PLACE_ORDER) {
                    event.placeEvent?.articles?.forEach { a ->
                        EmitArticleValidation.emit(
                            event.orderId!!.toHexString(),
                            a.articleId!!
                        )
                    }
                }
            }.start()
        }
    }

    companion object {
        private var currentInstance: BatchService? = null

        fun instance(): BatchService {
            return currentInstance ?: BatchService().also {
                currentInstance = it
            }
        }
    }
}