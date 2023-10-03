package rest

import batch.BatchService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import security.TokenService
import security.validateTokenIsAdminUser
import utils.http.authHeader

/**
 * @api {get} /v1/orders_batch/validated Batch Validated
 * @apiName Batch Validated
 * @apiGroup Ordenes
 *
 * @apiDescription Ejecuta un proceso batch para ordenes en estado VALIDATED.
 *
 * @apiUse AuthHeader
 *
 * @apiSuccessExample {json} Respuesta
 *   HTTP/1.1 200 OK
 *
 *
 * @apiUse Errors
 */
class GetOrdersBatchValidated(
    private val batch: BatchService,
    private val tokenService: TokenService
) {
    fun init(app: Routing) = app.apply {
        get("/v1/orders_batch/validated") {
            this.call.authHeader.validateTokenIsAdminUser(tokenService)

            batch.processValidatedOrders();
            this.call.respond("")
        }
    }
}
