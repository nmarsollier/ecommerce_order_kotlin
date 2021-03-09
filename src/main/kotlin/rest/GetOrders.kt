package rest

import io.javalin.Javalin
import model.orders.projections.orderStatus.repository.OrderStatusRepository
import rest.dto.OrderListData
import utils.javalin.route

/**
 * @api {get} /v1/orders Ordenes de Usuario
 * @apiName Ordenes de Usuario
 * @apiGroup Ordenes
 *
 * @apiDescription Busca todas las ordenes del usuario logueado.
 *
 * @apiUse AuthHeader
 *
 *  @apiSuccessExample {json} Respuesta
 *   HTTP/1.1 200 OK
 *   [{
 *      "id": "{orderID}",
 *      "status": "{Status}",
 *      "cartId": "{cartId}",
 *      "updated": "{updated date}",
 *      "created": "{created date}",
 *      "totalPrice": {price}
 *      "articles": {count}
 *   }, ...
 *   ]
 * @apiUse Errors
 */
class GetOrders private constructor(
    private val repository: OrderStatusRepository = OrderStatusRepository.instance()
) {
    private fun init(app: Javalin) {
        app.get(
            "/v1/orders",
            route(
                validateUser
            ) {
                val orders = repository.findByUserId(it.currentUser().id)

                it.json(orders.map {
                    OrderListData(it)
                })
            })
    }

    companion object {
        var currentInstance: GetOrders? = null

        fun init(app: Javalin) {
            currentInstance ?: GetOrders().also {
                it.init(app)
                currentInstance = it
            }
        }
    }
}

