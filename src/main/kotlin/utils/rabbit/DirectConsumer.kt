package utils.rabbit

import com.rabbitmq.client.*
import utils.env.Environment
import utils.gson.jsonToObject
import utils.validator.validate
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Escuchar en una cola direct es recibir un mensaje directo,
 * Necesitamos un exchange y un queue especifico para enviar correctamente el mensaje.
 * Tanto el consumer como el publisher deben compartir estos mismos datos.
 */
class DirectConsumer(private val exchange: String, private val queue: String) {
    private val listeners: MutableMap<String, EventProcessor> = HashMap()
    fun addProcessor(event: String, listener: EventProcessor) {
        listeners[event] = listener
    }

    /**
     * En caso de desconexión se conectara nuevamente en 10 segundos
     */
    private fun startDelayed() {
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
            factory.host = Environment.env.rabbitServerUrl
            val connection: Connection = factory.newConnection()
            val channel: Channel = connection.createChannel()
            channel.exchangeDeclare(exchange, "direct")
            channel.queueDeclare(queue, false, false, false, null)
            channel.queueBind(queue, exchange, queue)
            Thread {
                try {
                    Logger.getLogger("RabbitMQ").log(Level.INFO, "RabbitMQ Escuchando $queue")
                    channel.basicConsume(queue, true, EventConsumer(channel))
                } catch (e: Exception) {
                    Logger.getLogger("RabbitMQ").log(Level.SEVERE, "RabbitMQ ", e)
                    startDelayed()
                }
            }.start()
        } catch (e: Exception) {
            Logger.getLogger("RabbitMQ").log(Level.SEVERE, "RabbitMQ ", e)
            startDelayed()
        }
    }

    internal inner class EventConsumer(channel: Channel?) : DefaultConsumer(channel) {
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
                    Logger.getLogger("RabbitMQ").log(Level.INFO, "RabbitMQ Consume model.article-data : " + event.type)
                    it.process(event)
                }
            } catch (e: Exception) {
                Logger.getLogger("RabbitMQ").log(Level.SEVERE, "RabbitMQ ", e)
            }
        }
    }
}