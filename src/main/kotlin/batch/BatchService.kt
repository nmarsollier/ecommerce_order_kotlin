package batch

import events.repository.EventRepository
import events.repository.EventType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import projections.order.OrderService
import projections.order.repository.Status
import projections.orderStatus.repository.OrderStatusRepository
import rabbit.EmitArticleValidation
import java.util.concurrent.atomic.AtomicBoolean

class BatchService(
    private val orderService: OrderService,
    private val statusRepository: OrderStatusRepository,
    private val eventRepository: EventRepository,
    private val emitArticleValidation: EmitArticleValidation
) {
    private val placeOrdersRunning: AtomicBoolean = AtomicBoolean()
    private val validatedOrdersRunning: AtomicBoolean = AtomicBoolean()

    suspend fun processPlacedOrders() = CoroutineScope(Dispatchers.IO).launch {
        if (placeOrdersRunning.compareAndSet(false, true)) {
            statusRepository.findByStatus(Status.PLACED).forEach { stat ->
                stat.id?.let {
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
            }
            placeOrdersRunning.set(false)
        }
    }

    fun processValidatedOrders() = CoroutineScope(Dispatchers.IO).launch {
        if (validatedOrdersRunning.compareAndSet(false, true)) {
            statusRepository.findByStatus(Status.VALIDATED).forEach { stat ->
                stat.id?.let {
                    orderService.buildOrder(stat.id)?.let {
                        statusRepository.save(stat.update(it))
                    }
                }
            }
            validatedOrdersRunning.set(false)
        }
    }

    fun processPaymentDefinedOrders() = CoroutineScope(Dispatchers.IO).launch {
        if (validatedOrdersRunning.compareAndSet(false, true)) {
            statusRepository.findByStatus(Status.PAYMENT_DEFINED).forEach { stat ->
                stat.id?.let {
                    orderService.buildOrder(stat.id)?.let { order ->
                        statusRepository.save(stat.update(order))
                    }
                }
            }
            validatedOrdersRunning.set(false)
        }
    }

    private fun validateOrder(orderId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val event = eventRepository.findPlaceByOrderId(orderId)
            if (event != null) {
                /**
                 * Busca todos los artículos de un evento, los envía a rabbit para que catalog valide si están activos
                 */
                if (event.type === EventType.PLACE_ORDER) {
                    event.placeEvent?.articles?.forEach { a ->
                        emitArticleValidation.emit(
                            event.orderId!!,
                            a.articleId!!
                        )
                    }
                }
            }
        }
    }
}