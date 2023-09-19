package rabbit

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import model.orders.events.EventService
import model.orders.events.dto.NewPlaceData
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
class ConsumePlaceOrder private constructor(
    private val service: EventService = EventService.instance()
) {
    private fun init() {
        DirectConsumer("order", "order").apply {
            addProcessor("place-order") { e: RabbitEvent? -> processPlaceOrder(e) }
            start()
        }
    }


    private fun processPlaceOrder(event: RabbitEvent?) = MainScope().launch {
        event?.message?.toString()?.jsonToObject<NewPlaceData>()?.let { cart ->
            try {
                val data = service.placeOrder(cart)
                EmitOrderPlaced.emit(data)
            } catch (e: Exception) {
                Log.error(e)
            }
        }
    }

    companion object {
        private var currentInstance: ConsumePlaceOrder? = null

        fun init() {
            currentInstance ?: ConsumePlaceOrder().also {
                it.init()
                currentInstance = it
            }
        }
    }
}