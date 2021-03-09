package rest

import io.javalin.Javalin
import model.batch.BatchService
import utils.javalin.route

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
class GetOrdersBatchPaymentDefined private constructor(
    private val batch: BatchService = BatchService.instance()
) {
    private fun init(app: Javalin) {
        app.get(
            "/v1/orders_batch/payment_defined",
            route(
                validateAdminUser
            ) {
                batch.processPaymentDefinedOrders();
                it.json("")
            })
    }

    companion object {
        var currentInstance: GetOrdersBatchPaymentDefined? = null

        fun init(app: Javalin) {
            currentInstance ?: GetOrdersBatchPaymentDefined().also {
                it.init(app)
                currentInstance = it
            }
        }
    }
}
