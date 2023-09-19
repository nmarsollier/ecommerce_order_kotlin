package utils.rabbit

import com.rabbitmq.client.*
import utils.env.Environment
import utils.gson.jsonToObject
import utils.validator.validate
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Las colas fanout son un broadcast, no necesitan queue, solo exchange que es donde se publican
 */
class FanoutConsumer(private val exchange: String) {
    private val listeners: MutableMap<String, EventProcessor> = HashMap()
    fun addProcessor(event: String, listener: EventProcessor) {
        listeners[event] = listener
    }

    /**
     * En caso de desconexión se conectara nuevamente en 10 segundos
     */
    fun startDelayed() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                start()
            }
        }, (10 * 1000).toLong()) // En 10 segundos reintenta.
    }

    /**
     * Conecta a rabbit para escuchar eventos
     */
    fun start() {
        try {
            val factory = ConnectionFactory()
            factory.setHost(Environment.env.rabbitServerUrl)
            val connection: Connection = factory.newConnection()
            val channel: Channel = connection.createChannel()
            channel.exchangeDeclare(exchange, "fanout")
            val queueName: String = channel.queueDeclare("", false, false, false, null).getQueue()
            channel.queueBind(queueName, exchange, "")
            Thread {
                try {
                    Logger.getLogger("RabbitMQ").log(Level.INFO, "RabbitMQ Fanout Conectado")
                    val consumer: Consumer = object : DefaultConsumer(channel) {
                        override fun handleDelivery(
                            consumerTag: String?,  //
                            envelope: Envelope?,  //
                            properties: AMQP.BasicProperties?,  //
                            body: ByteArray?
                        ) {
                            try {
                                body ?: return

                                val event = String(body).jsonToObject<RabbitEvent>() ?: return
                                event.validate()
                                listeners[event.type]?.let {
                                    Logger.getLogger("RabbitMQ").log(Level.INFO, "RabbitMQ Consume " + event.type)
                                    it.process(event)
                                }
                            } catch (e: Exception) {
                                Logger.getLogger("RabbitMQ").log(Level.SEVERE, "RabbitMQ Logout", e)
                            }
                        }
                    }
                    channel.basicConsume(queueName, true, consumer)
                } catch (e: Exception) {
                    Logger.getLogger("RabbitMQ").log(Level.INFO, "RabbitMQ ArticleValidation desconectado")
                    startDelayed()
                }
            }.start()
        } catch (e: Exception) {
            Logger.getLogger("RabbitMQ").log(Level.INFO, "RabbitMQ ArticleValidation desconectado")
            startDelayed()
        }
    }
}