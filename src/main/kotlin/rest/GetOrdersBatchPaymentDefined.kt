package rest

import batch.BatchService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import security.TokenService
import security.validateTokenIsAdminUser
import utils.http.authHeader

/**
 * @api {get} /v1/orders_batch/payment_defined Batch Payment Defined
 * @apiName Batch Payment Defined
 * @apiGroup Ordenes
 *
 * @apiDescription Ejecuta un proceso batch que chequea ordenes en estado PAYMENT_DEFINED.
 *
 * @apiUse AuthHeader
 *
 * @apiSuccessExample {json} Respuesta
 *   HTTP/1.1 200 OK
 *
 *
 * @apiUse Errors
 */
class GetOrdersBatchPaymentDefined(
    private val batch: BatchService,
    private val tokenService: TokenService
) {
    fun init(app: Routing) = app.apply {
        get("/v1/orders_batch/payment_defined") {
            this.call.authHeader.validateTokenIsAdminUser(tokenService)

            batch.processPaymentDefinedOrders();
            this.call.respond("")
        }
    }
}
