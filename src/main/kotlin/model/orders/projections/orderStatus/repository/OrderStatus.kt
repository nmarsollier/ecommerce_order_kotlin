package model.orders.projections.orderStatus.repository

import model.orders.events.repository.Event
import model.orders.projections.order.repository.Order
import model.orders.projections.order.repository.Status
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Indexed
import java.util.*

/**
 * Es el Agregado principal de Articulo.
 */
@Entity(value = "order_projection", noClassnameStored = true)
data class OrderStatus(
    @Id
    val id: ObjectId? = null,

    @Indexed
    val status: Status? = null,

    @Indexed
    val userId: String? = null,

    @Indexed
    val cartId: String? = null,
    val articles: Int = 0,
    val payment: Double = 0.0,
    val totalPrice: Double = 0.0,
    val updated: Date = Date(),
    val created: Date = Date()

) {
    fun update(event: Event): OrderStatus {
        return getUpdaterForEvent(event.type)(this, event)
    }

    fun update(order: Order): OrderStatus {
        return this.copy(
            status = order.status,
            totalPrice = order.totalPrice
        )
    }
}