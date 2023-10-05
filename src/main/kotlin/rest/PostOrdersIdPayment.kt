package rest

import events.EventService
import io.ktor.server.application.*
import io.ktor.server.routing.*
import projections.order.repository.asOrderId
import rabbit.dto.PaymentData
import security.TokenService
import security.validateTokenIsLoggedIn
import utils.http.authHeader

/**
 * @api {post} /v1/orders/:orderId/payment Agregar Pago
 * @apiName Agrega un Pago
 * @apiGroup Pagos
 *
 * @apiUse AuthHeader
 *
 * @apiExample {json} Body
 *   {
 *       "paymentMethod": "CASH | CREDIT | DEBIT",
 *       "amount": "{amount}"
 *   }
 *
 * @apiSuccessExample {json} Respuesta
 *   HTTP/1.1 200 OK
 *
 * @apiUse Errors
 */
class PostOrdersIdPayment(
    private val service: EventService,
    private val tokenService: TokenService
) {
    fun init(app: Routing) = app.apply {
        post<PaymentData>("/v1/orders/{orderId}/payment") {
            val user = this.call.authHeader.validateTokenIsLoggedIn(tokenService)
            val id = this.call.parameters["orderId"].asOrderId

            val payment = it.copy(
                orderId = id,
                userId = user.id
            )
            service.placePayment(payment)
        }
    }
}

