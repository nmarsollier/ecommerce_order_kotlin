package model.orders.projections.order

import model.orders.events.repository.Event
import model.orders.events.repository.EventRepository
import model.orders.projections.order.repository.Order

class OrderService(
    val repository: EventRepository = EventRepository.instance()
) {
    // Se elimina y regenera la proyección a partir de los eventos.
    suspend fun buildOrder(orderId: String): Order? {
        val events: List<Event> = repository.findByOrderId(orderId)

        if (events.isEmpty()) {
            return null
        }

        var order = Order()
        events.forEach {
            order = order.update(it)
        }
        return order
    }

    companion object {
        private var currentInstance: OrderService? = null

        fun instance(): OrderService {
            return currentInstance ?: OrderService().also {
                currentInstance = it
            }
        }
    }
}