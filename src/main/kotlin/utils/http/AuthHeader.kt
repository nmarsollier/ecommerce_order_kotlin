package utils.http

import io.ktor.server.application.*
import io.ktor.server.request.*

val ApplicationCall.authHeader: String?
    get() = request.header("Authorization")
