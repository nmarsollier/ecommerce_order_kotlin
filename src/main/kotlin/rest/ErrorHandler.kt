package rest

import io.javalin.Javalin
import utils.env.Log
import utils.errors.SimpleError
import utils.errors.UnauthorizedError
import utils.errors.ValidationError


/**
 * Es un Helper para serializar correctamente los errores del sistema.
 *
 * @apiDefine Errors
 *
 * @apiErrorExample 400 Bad Request
 *     HTTP/1.1 400 Bad Request
 *     {
 *         "path" : "{Nombre de la propiedad}",
 *         "message" : "{Motivo del error}"
 *     }
 *
 * @apiErrorExample 400 Bad Request
 *     HTTP/1.1 400 Bad Request
 *     {
 *         "error" : "{Motivo del error}"
 *     }
 *
 * @apiErrorExample 500 Server Error
 *     HTTP/1.1 500 Server Error
 *     {
 *         "error" : "{Motivo del error}"
 *     }
 */
class ErrorHandler private constructor() {
    private val INTERNAL_ERROR = mapOf("error" to "Internal Server Error")

    private fun init(app: Javalin) {
        app.exception(ValidationError::class.java) { ex, ctx ->
            Log.error(ex)
            ctx.status(400).json(ex.json())
        }

        app.exception(SimpleError::class.java) { ex, ctx ->
            Log.error(ex)
            ctx.status(400).json(ex.json())
        }

        app.exception(UnauthorizedError::class.java) { ex, ctx ->
            Log.error(ex)
            ctx.status(401).json(ex.json())
        }

        app.exception(Exception::class.java) { ex, ctx ->
            Log.error(ex)
            ctx.status(500).json(INTERNAL_ERROR)
        }
    }

    companion object {
        var currentInstance: ErrorHandler? = null

        fun init(app: Javalin) {
            currentInstance ?: ErrorHandler().also {
                it.init(app)
                currentInstance = it
            }
        }
    }
}