package utils.rabbit

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import utils.env.Environment
import utils.gson.toJson
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Publicar en una cola direct es enviar un mensaje directo a un destinatario en particular,
 * Necesitamos un exchange y un queue especifico para enviar correctamente el mensaje.
 * Tanto el consumer como el publisher deben compartir estos mismos datos.
 */
object DirectPublisher {
    fun publish(exchange: String?, queue: String?, message: RabbitEvent) {
        try {
            val factory = ConnectionFactory()
            factory.setHost(Environment.env.rabbitServerUrl)
            val connection: Connection = factory.newConnection()
            val channel: Channel = connection.createChannel()
            channel.exchangeDeclare(exchange, "direct")
            channel.queueDeclare(queue, false, false, false, null)
            channel.queueBind(queue, exchange, queue)
            channel.basicPublish(exchange, queue, null, message.toJson().toByteArray())
            Logger.getLogger("RabbitMQ").log(Level.INFO, "RabbitMQ Emit " + message.type)
        } catch (e: Exception) {
            Logger.getLogger("RabbitMQ").log(Level.SEVERE, "RabbitMQ no se pudo encolar " + message.type, e)
        }
    }
}