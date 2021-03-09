package rest

import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import io.javalin.plugin.json.FromJsonMapper
import io.javalin.plugin.json.JavalinJson
import io.javalin.plugin.json.ToJsonMapper
import utils.env.Environment
import utils.gson.gson

class Routes private constructor() {
    companion object {

        fun init() {
            val gson = gson()

            JavalinJson.fromJsonMapper = object : FromJsonMapper {
                override fun <T> map(json: String, targetClass: Class<T>) = gson.fromJson(json, targetClass)
            }

            JavalinJson.toJsonMapper = object : ToJsonMapper {
                override fun map(obj: Any): String = gson.toJson(obj)
            }

            val app = Javalin.create {
                it.enableCorsForAllOrigins()
                it.addStaticFiles(Environment.env.staticLocation, Location.EXTERNAL)
            }.start(Environment.env.serverPort)

            ErrorHandler.init(app)
            GetOrders.init(app)
            GetOrdersBatchPaymentDefined.init(app)
            GetOrdersBatchPlaced.init(app)
            GetOrdersBatchValidated.init(app)
            GetOrdersId.init(app)
            PostOrdersIdPayment.init(app)
        }
    }
}
