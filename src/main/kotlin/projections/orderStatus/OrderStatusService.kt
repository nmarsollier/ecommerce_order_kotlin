package projections.orderStatus

import events.repository.Event
import events.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import projections.orderStatus.repository.OrderStatus
import projections.orderStatus.repository.OrderStatusRepository

class OrderStatusService(
    val orderRepository: OrderStatusRepository = OrderStatusRepository.instance(),
    val eventRepository: EventRepository = EventRepository.instance()
) {
    // Actualiza la proyección Order
    fun update(event: Event) = CoroutineScope(Dispatchers.IO).launch {
        val orderId = event.orderId ?: return@launch
        var order: OrderStatus? = orderRepository.findById(orderId)

        if (order == null) {
            order = OrderStatus()
        }

        order = order.update(event)
        orderRepository.save(order)
    }

    suspend fun findById(orderId: String?): OrderStatus? {
        orderId ?: return null
        var order: OrderStatus? = orderRepository.findById(orderId)
        if (order == null) {
            order = rebuildOrderStatus(orderId)
        }
        return order
    }

    // Se elimina y regenera la proyección a partir de los eventos.
    suspend fun rebuildOrderStatus(orderId: String?): OrderStatus? {
        orderId ?: return null
        val events: List<Event> = eventRepository.findByOrderId(orderId)
        if (events.isEmpty()) {
            return null
        }
        orderRepository.delete(orderId)

        var order = OrderStatus()
        events.forEach {
            order = order.update(it)
        }

        orderRepository.save(order)
        return order
    }

    companion object {
        private var currentInstance: OrderStatusService? = null

        fun instance(): OrderStatusService {
            return currentInstance ?: OrderStatusService().also {
                currentInstance = it
            }
        }
    }
}