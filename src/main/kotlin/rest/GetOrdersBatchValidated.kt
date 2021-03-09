package rest

import io.javalin.Javalin
import model.batch.BatchService
import utils.javalin.route

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
class GetOrdersBatchValidated private constructor(
    private val batch: BatchService = BatchService.instance()
) {
    private fun init(app: Javalin) {
        app.get(
            "/v1/orders_batch/validated",
            route(
                validateAdminUser
            ) {
                batch.processValidatedOrders();
                it.json("")
            })
    }

    companion object {
        var currentInstance: GetOrdersBatchValidated? = null

        fun init(app: Javalin) {
            currentInstance ?: GetOrdersBatchValidated().also {
                it.init(app)
                currentInstance = it
            }
        }
    }
}
