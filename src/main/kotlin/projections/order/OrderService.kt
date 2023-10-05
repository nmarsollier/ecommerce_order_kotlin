package projections.order

import events.repository.EventRepository
import projections.order.repository.Order

class OrderService(
    val repository: EventRepository
) {
    // Se elimina y regenera la proyección a partir de los eventos.
    suspend fun buildOrder(orderId: String): Order? {
        var order = Order()
        return repository.findByOrderId(orderId)
            .takeIf { it.isNotEmpty() }
            ?.forEach {
                order = order.update(it)
            }
            ?.let {
                order
            }
    }
}