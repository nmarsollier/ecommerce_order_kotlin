package rabbit

import security.TokenService
import utils.rabbit.FanoutConsumer
import utils.rabbit.RabbitEvent

class ConsumeAuthLogout(
    private val tokenService: TokenService
) {
    fun init() {
        FanoutConsumer("auth").apply {
            addProcessor("logout") { e: RabbitEvent? -> processLogout(e) }
            start()
        }
    }

    /**
     * @api {fanout} auth/logout Logout
     *
     * @apiGroup RabbitMQ GET
     *
     * @apiDescription Escucha de mensajes logout desde auth. Invalida sesiones en cache.
     *
     * @apiExample {json} Mensaje
     * {
     * "type": "article-exist",
     * "message" : "tokenId"
     * }
     */
    private fun processLogout(event: RabbitEvent?) {
        event?.message?.toString()?.let {
            tokenService.invalidateTokenCache(it)
        }
    }
}
