package rest

import io.javalin.http.Context
import model.security.TokenService
import model.security.dao.User
import model.security.validateAdminUser
import model.security.validateUser
import org.bson.types.ObjectId
import utils.env.Log
import utils.errors.UnauthorizedError
import utils.errors.ValidationError
import utils.javalin.NextFun

val logTimes = { ctx: Context, next: NextFun ->
    val currTime = System.currentTimeMillis()
    next()
    Log.info("Time consumed ${ctx.path()} = ${(System.currentTimeMillis() - currTime)} ms")
}

val validateAdminUser = { ctx: Context, _: NextFun ->
    val authHeader = ctx.header("Authorization") ?: throw UnauthorizedError()
    TokenService.instance().validateAdminUser(authHeader)
}

val validateUser = { ctx: Context, _: NextFun ->
    val authHeader = ctx.header("Authorization") ?: throw UnauthorizedError()
    TokenService.instance().validateUser(authHeader)
}

fun Context.currentUser(): User {
    return TokenService.instance().getUserByToken(this.header("Authorization") ?: "")
}

val validateOrderId = { ctx: Context, _: NextFun ->
    try {
        val id = ctx.pathParam("orderId")

        if (id.isBlank()) {
            throw ValidationError().addPath("id", "Not found")
        }

        ObjectId(id)
    } catch (e: Exception) {
        Log.error(e)
        throw ValidationError().addPath("id", "Not found")
    }
    Unit
}