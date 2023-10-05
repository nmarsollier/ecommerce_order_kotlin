package batch

import events.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import projections.order.OrderService
import projections.order.repository.Status
import projections.orderStatus.repository.OrderStatusRepository
import projections.orderStatus.repository.saveIn
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
        if (!placeOrdersRunning.compareAndSet(false, true)) {
            return@launch
        }
        statusRepository.findByStatus(Status.PLACED).forEach { stat ->
            stat.id?.let {
                orderService.buildOrder(stat.id)?.let { order ->
                    // Si status de order sigue en PLACED es porque no se validó completamente
                    if (order.status === Status.PLACED) {
                        validateOrder(order.id!!)
                    } else {
                        // En este caso OrderStatus esta desactualizado
                        stat.update(order).saveIn(statusRepository)
                    }
                }
            }
        }
        placeOrdersRunning.set(false)
    }

    fun processValidatedOrders() = CoroutineScope(Dispatchers.IO).launch {
        if (!validatedOrdersRunning.compareAndSet(false, true)) {
            return@launch
        }

        statusRepository.findByStatus(Status.VALIDATED).forEach { stat ->
            stat.id?.let {
                orderService.buildOrder(stat.id)?.let {
                    stat.update(it).saveIn(statusRepository)
                }
            }
        }
        validatedOrdersRunning.set(false)
    }

    fun processPaymentDefinedOrders() = CoroutineScope(Dispatchers.IO).launch {
        if (!validatedOrdersRunning.compareAndSet(false, true)) {
            return@launch
        }
        statusRepository.findByStatus(Status.PAYMENT_DEFINED).forEach { stat ->
            stat.id?.let {
                orderService.buildOrder(stat.id)?.let { order ->
                    stat.update(order).saveIn(statusRepository)
                }
            }
        }
        validatedOrdersRunning.set(false)
    }

    private fun validateOrder(orderId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            eventRepository.findPlaceByOrderId(orderId)?.let { event ->
                /**
                 * Busca todos los artículos de un evento, los envía a rabbit para que catalog valide si están activos
                 */
                event.placeEvent?.articles?.forEach { a ->
                    emitArticleValidation.emit(
                        event.id!!,
                        a.articleId!!
                    )
                }
            }
        }
    }
}