package rest

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import utils.errors.SimpleError
import utils.errors.UnauthorizedError
import utils.errors.ValidationError

class ErrorHandler {
    fun init(app: Application) = app.apply {
        install(StatusPages) {
            exception<Throwable> { call, throwable ->
                when (throwable) {
                    is ValidationError -> {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            throwable.json()
                        )
                    }

                    is SimpleError -> {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            throwable.json()
                        )
                    }

                    is UnauthorizedError -> {
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            throwable.json()
                        )
                    }

                    is Exception -> {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("error" to throwable.message)
                        )
                    }
                }
            }
        }
    }
}