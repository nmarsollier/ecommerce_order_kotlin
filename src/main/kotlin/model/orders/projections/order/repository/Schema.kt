package model.orders.projections.order.repository

import model.orders.events.repository.Event
import model.orders.events.repository.PaymentEvent
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Indexed
import java.util.*

/**
 * Es el Agregado principal de Articulo.
 */
@Entity(value = "order_projection", noClassnameStored = true)
data class Order(
    @Id
    var id: ObjectId? = null,
    var status: Status? = null,

    @Indexed
    var userId: String? = null,

    @Indexed
    var cartId: String? = null,
    var articles: List<Article> = emptyList(),
    var payment: List<Payment> = emptyList(),
    var updated: Date = Date(),
    var created: Date = Date()
) {
    fun update(event: Event): Order {
        return getUpdaterForEvent(event.type)(this, event)
    }

    val totalPrice: Double
        get() = articles.map { it.totalPrice }.sum()

    val totalPayment: Double
        get() = payment.map { it.amount }.sum()

    data class Article(
        val id: String? = null,
        val quantity: Int = 0,
        var unitaryPrice: Double = 0.0,
        var isValid: Boolean = false,
        var isValidated: Boolean = false
    ) {
        val totalPrice: Double
            get() = unitaryPrice * quantity
    }

    data class Payment(
        val method: PaymentEvent.Method? = null,
        val amount: Double = 0.0
    )
}

enum class Status {
    PLACED, INVALID, VALIDATED, PAYMENT_DEFINED
}