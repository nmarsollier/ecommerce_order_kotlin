package rest

import com.rabbitmq.tools.json.JSONUtil
import io.javalin.Javalin
import io.javalin.config.JavalinConfig
import io.javalin.http.InternalServerErrorResponse
import io.javalin.json.JavalinJackson
import io.javalin.json.JsonMapper
import io.javalin.json.PipedStreamUtil
import io.javalin.plugin.bundled.CorsContainer
import io.javalin.util.CoreDependency
import io.javalin.util.DependencyUtil
import io.javalin.util.JavalinLogger
import io.javalin.util.Util
import utils.env.Environment
import utils.gson.gson
import utils.gson.toJson
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.reflect.Type
import java.util.function.Consumer
import java.util.stream.Stream

class Routes private constructor() {
    companion object {

        fun init() {
            val gsonMapper: JsonMapper = object : JsonMapper {
                override fun toJsonString(obj: Any, type: Type): String {
                    return gson().toJson()
                }

                override fun <T : Any> fromJsonString(json: String, targetType: Type): T {
                    return gson().fromJson(json, targetType)
                }

                override fun toJsonStream(obj: Any, type: Type): InputStream = when (obj) {
                    is String -> obj.byteInputStream()
                    else -> obj.toJson().byteInputStream()
                }

                override fun writeToOutputStream(stream: Stream<*>, outputStream: OutputStream) {
                    stream.forEach { outputStream.write(it.toJson().toByteArray()) }
                }

                override fun <T : Any> fromJsonStream(json: InputStream, targetType: Type): T {
                    return gson().fromJson(InputStreamReader(json), targetType)
                }
            }

            val app = Javalin.create { config: JavalinConfig ->
                config.jsonMapper(gsonMapper)
                config.plugins.enableCors { cors: CorsContainer ->
                    cors.add { it.anyHost() }
                }
                //config.staticFiles.add(Environment.env.staticLocation)
            }
            app.start(Environment.env.serverPort)

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
