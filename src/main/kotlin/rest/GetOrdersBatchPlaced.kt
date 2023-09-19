package rest

import io.javalin.Javalin
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import model.batch.BatchService
import utils.javalin.route

/**
 * @api {get} /v1/orders_batch/placed Batch Placed
 * @apiName Batch Placed
 * @apiGroup Ordenes
 *
 * @apiDescription Ejecuta un proceso batch que chequea ordenes en estado PLACED.
 *
 * @apiUse AuthHeader
 *
 * @apiSuccessExample {json} Respuesta
 *   HTTP/1.1 200 OK
 *
 *
 * @apiUse Errors
 */
class GetOrdersBatchPlaced private constructor(
    private val batch: BatchService = BatchService.instance()
) {
    private fun init(app: Javalin) {
        app.get(
            "/v1/orders_batch/placed",
            route(
                validateAdminUser
            ) {
                MainScope().launch {
                    batch.processPlacedOrders();
                    it.json("")
                }
            })
    }

    companion object {
        var currentInstance: GetOrdersBatchPlaced? = null

        fun init(app: Javalin) {
            currentInstance ?: GetOrdersBatchPlaced().also {
                it.init(app)
                currentInstance = it
            }
        }
    }
}
