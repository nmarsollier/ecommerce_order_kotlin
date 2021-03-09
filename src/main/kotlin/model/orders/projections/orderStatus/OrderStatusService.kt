package model.orders.projections.orderStatus

import model.orders.events.repository.Event
import model.orders.events.repository.EventRepository
import model.orders.projections.orderStatus.repository.OrderStatus
import model.orders.projections.orderStatus.repository.OrderStatusRepository
import org.bson.types.ObjectId

class OrderStatusService(
    val orderRepository: OrderStatusRepository = OrderStatusRepository.instance(),
    val eventRepository: EventRepository = EventRepository.instance()
) {
    // Actualiza la proyección Order
    fun update(event: Event) {
        val orderId = event.orderId ?: return
        var order: OrderStatus? = orderRepository.findById(orderId)

        if (order == null) {
            order = OrderStatus()
        }

        order = order.update(event)
        orderRepository.save(order)
    }

    fun findById(orderId: ObjectId?): OrderStatus? {
        orderId ?: return null
        var order: OrderStatus? = orderRepository.findById(orderId)
        if (order == null) {
            order = rebuildOrderStatus(orderId)
        }
        return order
    }

    // Se elimina y regenera la proyección a partir de los eventos.
    fun rebuildOrderStatus(orderId: ObjectId?): OrderStatus? {
        orderId ?: return null
        val events: List<Event> = eventRepository.findByOrderId(orderId)
        if (events.size == 0) {
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