package rest

import io.javalin.Javalin
import model.orders.events.EventService
import model.orders.events.dto.PaymentData
import utils.errors.ValidationError
import utils.gson.jsonToObject
import utils.javalin.route

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
class PostOrdersIdPayment private constructor(
    private val service: EventService = EventService.instance()
) {
    private fun init(app: Javalin) {
        app.post(
            "/v1/orders/:orderId/payment",
            route(
                validateUser,
                validateOrderId
            ) {
                val orderId: String = it.pathParam("orderId")
                val user = it.currentUser()

                it.body().jsonToObject<PaymentData>()?.let { payment ->
                    payment.copy(
                        orderId = orderId,
                        userId = user.id
                    )
                    service.placePayment(payment)
                } ?: throw ValidationError().addPath("id", "Not found")
            })
    }

    companion object {
        var currentInstance: PostOrdersIdPayment? = null

        fun init(app: Javalin) {
            currentInstance ?: PostOrdersIdPayment().also {
                it.init(app)
                currentInstance = it
            }
        }
    }
}

