package model.orders.projections.orderStatus

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import model.orders.events.repository.Event
import model.orders.events.repository.EventRepository
import model.orders.projections.orderStatus.repository.OrderStatus
import model.orders.projections.orderStatus.repository.OrderStatusRepository

class OrderStatusService(
    val orderRepository: OrderStatusRepository = OrderStatusRepository.instance(),
    val eventRepository: EventRepository = EventRepository.instance()
) {
    // Actualiza la proyección Order
    fun update(event: Event) = MainScope().launch {
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