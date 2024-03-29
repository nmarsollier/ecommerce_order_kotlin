package rabbit

import events.EventService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rabbit.dto.NewArticleValidationData
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
class ConsumeArticleData(
    private val service: EventService
) {
    fun init() {
        DirectConsumer("order", "order").apply {
            addProcessor("article-data") { e: RabbitEvent? -> processArticleData(e) }
            start()
        }
    }

    private fun processArticleData(event: RabbitEvent?) = CoroutineScope(Dispatchers.IO).launch {
        event?.message?.toString()?.jsonToObject<NewArticleValidationData>()?.let { articleExist ->
            try {
                service.placeArticleExist(articleExist)
            } catch (e: Exception) {
                Log.error(e)
            }
        }
    }
}