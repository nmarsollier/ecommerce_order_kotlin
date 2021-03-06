package rabbit

import model.orders.events.EventService
import model.orders.events.dto.NewArticleValidationData
import utils.env.Log
import utils.gson.jsonToObject
import utils.rabbit.DirectConsumer
import utils.rabbit.RabbitEvent

/**
 *
 * @api {direct} order/article-data Validar Artículos
 *
 * @apiGroup RabbitMQ GET
 *
 * @apiDescription Antes de iniciar las operaciones se validan los artículos contra el catalogo.
 *
 * @apiExample {json} Mensaje
 *     {
 *     "type": "article-data",
 *     "message" : {
 *         "cartId": "{cartId}",
 *         "articleId": "{articleId}",
 *         "valid": True|False
 *        }
 *     }
 */
class ConsumeArticleData private constructor(
    private val service: EventService = EventService.instance()
) {
    private fun init() {
        DirectConsumer("order", "order").apply {
            addProcessor("article-data") { e: RabbitEvent? -> processArticleData(e) }
            start()
        }
    }

    private fun processArticleData(event: RabbitEvent?) {
        event?.message?.toString()?.jsonToObject<NewArticleValidationData>()?.let { articleExist ->
            try {
                service.placeArticleExist(articleExist)
            } catch (e: Exception) {
                Log.error(e)
            }
        }
    }

    companion object {
        private var currentInstance: ConsumeArticleData? = null

        fun init() {
            currentInstance ?: ConsumeArticleData().also {
                it.init()
                currentInstance = it
            }
        }
    }
}