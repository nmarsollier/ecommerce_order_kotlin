package rabbit

import com.google.gson.annotations.SerializedName
import utils.gson.toJson
import utils.rabbit.DirectPublisher
import utils.rabbit.RabbitEvent

/**
 *
 * @api {direct} cart/article-data Validación de Artículos
 *
 * @apiGroup RabbitMQ POST
 *
 * @apiDescription Antes de iniciar las operaciones se validan los artículos contra el catalogo.
 *
 * @apiSuccessExample {json} Mensaje
 *     {
 *     "type": "article-data",
 *     "message" : {
 *         "cartId": "{cartId}",
 *         "articleId": "{articleId}",
 *        }
 *     }
 */
class EmitArticleValidation {
    fun emit(orderId: String, articleId: String) {
        val eventToSend = RabbitEvent(
            type = "article-data",
            exchange = "order",
            queue = "order",
            message = ArticleValidationData(orderId, articleId)
        )

        DirectPublisher.publish(eventToSend.exchange, eventToSend.queue, eventToSend)
    }
}


data class ArticleValidationData(
    @SerializedName("referenceId")
    val referenceId: String,

    @SerializedName("articleId")
    val articleId: String
)