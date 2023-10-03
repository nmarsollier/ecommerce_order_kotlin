package rabbit

import com.google.gson.annotations.SerializedName
import events.repository.Event
import events.repository.PlaceEvent
import utils.rabbit.DirectPublisher
import utils.rabbit.RabbitEvent

/**
 *
 * @api {topic} order/order-placed Orden Creada
 *
 * @apiGroup RabbitMQ POST
 *
 * @apiDescription Envía de mensajes order-placed desde Order con el topic "order_placed".
 *
 * @apiSuccessExample {json} Mensaje
 *     {
 *     "type": "order-placed",
 *     "message" : {
 *         "cartId": "{cartId}",
 *         "orderId": "{orderId}"
 *         "articles": [{
 *              "articleId": "{article id}"
 *              "quantity" : {quantity}
 *          }, ...]
 *        }
 *     }
 *
 */
class EmitOrderPlaced {
    fun emit(event: Event) {
        val eventToSend = RabbitEvent(
            type = "order-placed",
            exchange = "order",
            queue = "order",
            message = OrderPlacedResponse(
                event.orderId!!,
                event.placeEvent!!.cartId,
                event.placeEvent!!.articles
            )
        )

        DirectPublisher.publish(eventToSend.exchange, eventToSend.queue, eventToSend)
    }
}


class OrderPlacedResponse constructor(
    @SerializedName("orderId") var orderId: String?,
    @SerializedName("cartId") var cartId: String?,
    articles: List<PlaceEvent.Article>
) {

    @SerializedName("articles")
    private var articles = articles.map {
        Article(it.articleId, it.quantity)
    }

    data class Article(
        @SerializedName("articleId")
        val articleId: String? = null,

        @SerializedName("quantity")
        val quantity: Int = 0
    )
}