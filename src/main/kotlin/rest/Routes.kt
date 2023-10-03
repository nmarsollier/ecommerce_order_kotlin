package rest

import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import utils.env.Environment
import java.io.File

class Routes(
    private val getOrders: GetOrders,
    private val getOrdersId: GetOrdersId,
    private val getOrdersBatchPaymentDefined: GetOrdersBatchPaymentDefined,
    private val getOrdersBatchPlaced: GetOrdersBatchPlaced,
    private val getOrdersBatchValidated: GetOrdersBatchValidated,
    private val postOrdersIdPayment: PostOrdersIdPayment
) {

    fun init() {
        embeddedServer(
            Netty,
            port = Environment.env.serverPort,
            module = {
                install(CORS) {
                    anyHost()
                    allowMethod(HttpMethod.Options)
                    allowMethod(HttpMethod.Put)
                    allowMethod(HttpMethod.Patch)
                    allowMethod(HttpMethod.Delete)
                    allowHeader(HttpHeaders.ContentType)
                    allowHeader(HttpHeaders.Authorization)
                }
                install(ContentNegotiation) {
                    gson()
                }
                install(CallLogging)

                ErrorHandler().init(this)

                routing {
                    staticFiles("/", File(Environment.env.staticLocation))

                    getOrders.init(this)
                    getOrdersBatchPaymentDefined.init(this)
                    getOrdersBatchPlaced.init(this)
                    getOrdersBatchValidated.init(this)
                    getOrdersId.init(this)
                    postOrdersIdPayment.init(this)
                }
            }
        ).start(wait = true)
    }
}
