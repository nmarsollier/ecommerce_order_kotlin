package utils.javalin

import io.javalin.http.Context

typealias NextFun = () -> Unit
typealias RouteFunc = (ctx: Context, next: NextFun) -> Unit
typealias HandlerFunc = (ctx: Context) -> Unit

/**
 * Chain of responsibility router pattern
 *
 * call function route to create an spark handler function
 * @param routes Are the route middleware, yout can throw an exception to abort.
 *              next() function is provided to do actions before and after next executions,
 *              you don't need to call next() for normal execution, all middlewares will
 *              be executed sequentially
 *
 * @param handler The final handler, here you answer the route response
 *
 */
fun route(vararg routes: RouteFunc, handler: HandlerFunc): HandlerFunc {
    return { ctx ->
        var called = false
        val functionList = mutableListOf<NextFun>()
        functionList.add(0) {
            handler(ctx)
        }

        for (i in routes.indices.reversed()) {
            val next = functionList.first()

            val nextCalled = {
                next()
                called = true
            }

            functionList.add(0) {
                routes[i](ctx, nextCalled)
                if (!called) {
                    next()
                }
            }
        }

        functionList.first().invoke()
    }
}
