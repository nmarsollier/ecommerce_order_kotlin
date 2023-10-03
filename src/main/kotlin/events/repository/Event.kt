package events.repository

import org.bson.BsonType
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonRepresentation
import java.util.*

/**
 * Permite almacenar los eventos del event store.
 */
data class Event(
    @BsonId
    @BsonRepresentation(BsonType.OBJECT_ID)
    val id: String? = null,

    var orderId: String? = null,

    /**
     * Tipo de evento, es mejor que hacer herencia, mucho mas claro
     */
    var type: EventType? = null,
    var placeEvent: PlaceEvent? = null,
    var articleValidationEvent: ArticleValidationEvent? = null,
    var payment: PaymentEvent? = null,

    val created: Date = Date()
) {
    companion object {
        // Crea un nuevo evento de place order
        fun newPlaceOrder(placeEvent: PlaceEvent?) = Event(
            type = EventType.PLACE_ORDER,
            placeEvent = placeEvent
        )

        fun newArticleValidation(
            orderId: String?,
            validationEvent: ArticleValidationEvent?
        ) = Event(
            orderId = orderId,
            type = EventType.ARTICLE_VALIDATION,
            articleValidationEvent = validationEvent
        )

        fun newPayment(
            orderId: String?,
            userId: String?,
            method: PaymentEvent.Method?,
            amount: Double
        ) = Event(
            orderId = orderId,
            type = EventType.PAYMENT,
            payment = PaymentEvent(userId, method, amount)
        )
    }
}

/**
 * Tipo de evento, es mejor que hacer herencia, mucho mas claro
 */
enum class EventType {
    PLACE_ORDER, ARTICLE_VALIDATION, PAYMENT
}

/**
 * Un Evento Place
 */
data class PlaceEvent(
    val cartId: String?,
    val userId: String?,
    val articles: List<Article>
) {
    data class Article(
        val articleId: String?,
        val quantity: Int = 0
    )
}

/**
 * Un evento Payment
 */
data class PaymentEvent(
    val userId: String? = null,
    val method: Method? = null,
    val amount: Double = 0.0
) {
    enum class Method {
        CASH, CREDIT, DEBIT
    }
}

/**
 * Un evento de validacion de articulos
 */
data class ArticleValidationEvent(
    val articleId: String? = null,
    val isValid: Boolean,
    val stock: Int,
    val price: Double,
)