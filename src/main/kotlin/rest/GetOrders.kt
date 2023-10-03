package rest

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import projections.orderStatus.repository.OrderStatusRepository
import security.TokenService
import security.validateTokenIsLoggedIn
import utils.http.authHeader

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
class GetOrders(
    private val repository: OrderStatusRepository,
    private val tokenService: TokenService
) {
    fun init(app: Routing) = app.apply {
        get("/v1/orders") {
            val user = this.call.authHeader.validateTokenIsLoggedIn(tokenService)

            this.call.respond(repository.findByUserId(user.id))
        }
    }
}
