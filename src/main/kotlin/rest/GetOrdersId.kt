package rest

import io.javalin.Javalin
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import model.orders.projections.order.OrderService
import utils.errors.UnauthorizedError
import utils.errors.ValidationError
import utils.javalin.route

/**
 * @api {get} /v1/orders/:orderId Buscar Orden
 * @apiName Buscar Orden
 * @apiGroup Ordenes
 *
 * @apiDescription Busca una order del usuario logueado, por su id.
 *
 * @apiUse AuthHeader
 *
 * @apiSuccessExample {json} Respuesta
 *   HTTP/1.1 200 OK
 *   {
 *      "id": "{orderID}",
 *      "status": "{Status}",
 *      "cartId": "{cartId}",
 *      "updated": "{updated date}",
 *      "created": "{created date}",
 *      "articles": [
 *         {
 *             "id": "{articleId}",
 *             "quantity": {quantity},
 *             "validated": true|false,
 *             "valid": true|false
 *         }, ...
 *     ]
 *   }
 *
 * @apiUse Errors
 */
class GetOrdersId private constructor(
    private val service: OrderService = OrderService.instance()
) {
    private fun init(app: Javalin) {
        app.get(
            "/v1/orders/:orderId",
            route(
                validateUser,
                validateOrderId
            ) {
                MainScope().launch {
                    val order = service.buildOrder(it.queryParam("orderId")!!)
                        ?: throw ValidationError().addPath("orderId", "Not Found")

                    if (!order.userId.equals(it.currentUser().id)) {
                        throw UnauthorizedError()
                    }

                    it.json(order)
                }
            })
    }

    companion object {
        var currentInstance: GetOrdersId? = null

        fun init(app: Javalin) {
            currentInstance ?: GetOrdersId().also {
                it.init(app)
                currentInstance = it
            }
        }
    }
}
