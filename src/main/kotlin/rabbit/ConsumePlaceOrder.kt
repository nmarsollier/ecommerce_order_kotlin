package rabbit

import events.EventService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rabbit.dto.NewPlaceData
import utils.env.Log
import utils.gson.jsonToObject
import utils.rabbit.DirectConsumer
import utils.rabbit.RabbitEvent

/**
 *
 * @api {direct} order/place-order Crear Orden
 *
 * @apiGroup RabbitMQ GET
 *
 * @apiDescription Escucha de mensajes place-order en el canal de order.
 *
 * @apiExample {json} Mensaje
 *     {
 *     "type": "place-order",
 *     "exchange" : "{Exchange name to reply}"
 *     "queue" : "{Queue name to reply}"
 *     "message" : {
 *         "cartId": "{cartId}",
 *         "articles": "[articleId, ...]",
 *     }
 */
class ConsumePlaceOrder(
    private val service: EventService,
    private val emitOrderPlaced: EmitOrderPlaced
) {
    fun init() {
        DirectConsumer("order", "order").apply {
            addProcessor("place-order") { e: RabbitEvent? -> processPlaceOrder(e) }
            start()
        }
    }


    private fun processPlaceOrder(event: RabbitEvent?) = CoroutineScope(Dispatchers.IO).launch {
        event?.message?.toString()?.jsonToObject<NewPlaceData>()?.let { cart ->
            try {
                val data = service.placeOrder(cart)
                emitOrderPlaced.emit(data)
            } catch (e: Exception) {
                Log.error(e)
            }
        }
    }
}