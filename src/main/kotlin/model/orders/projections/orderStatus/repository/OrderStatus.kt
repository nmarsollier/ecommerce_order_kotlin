package model.orders.projections.orderStatus.repository

import model.orders.events.repository.Event
import model.orders.projections.order.repository.Order
import model.orders.projections.order.repository.Status
import org.bson.BsonType
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonRepresentation
import java.util.*

/**
 * Es el Agregado principal de Articulo.
 */
data class OrderStatus(
    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    val id: String? = null,

    val status: Status? = null,

    val userId: String? = null,

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