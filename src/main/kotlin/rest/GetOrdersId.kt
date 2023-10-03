package rest

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import projections.order.OrderService
import projections.order.repository.asOrderId
import security.TokenService
import security.validateTokenIsLoggedIn
import utils.errors.UnauthorizedError
import utils.errors.ValidationError
import utils.http.authHeader

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
class GetOrdersId(
    private val service: OrderService,
    private val tokenService: TokenService
) {
    fun init(app: Routing) = app.apply {
        get("/v1/orders/{orderId}") {
            val user = this.call.authHeader.validateTokenIsLoggedIn(tokenService)
            val id = this.call.parameters["orderId"].asOrderId

            val order = service.buildOrder(id)
                ?: throw ValidationError().addPath("orderId", "Not Found")

            if (!order.userId.equals(user.id)) {
                throw UnauthorizedError()
            }

            this.call.respond(order)
        }
    }
}
