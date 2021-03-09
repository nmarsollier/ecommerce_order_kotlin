package utils.rabbit

import com.rabbitmq.client.ConnectionFactory
import utils.env.Environment.env
import utils.gson.toJson
import java.util.logging.Level
import java.util.logging.Logger

/**
 * La cola topic permite que varios consumidores escuchen el mismo evento
 * topic es muy importante por es el evento que se va a escuchar
 * Para que un consumer escuche los eventos debe estar conectado al mismo exchange y escuchar el topic adecuado
 * queue permite distribuir la carga de los mensajes entre distintos consumers, los consumers con el mismo queue name
 * comparten la carga de procesamiento de mensajes, es importante que se defina el queue
 */
object TopicPublisher {
    fun publish(exchange: String?, topic: String?, message: RabbitEvent) {
        try {
            val factory = ConnectionFactory()
            factory.host = env.rabbitServerUrl
            val connection = factory.newConnection()
            val channel = connection.createChannel()
            channel.exchangeDeclare(exchange, "topic")
            channel.basicPublish(exchange, topic, null, message.toJson().toByteArray())
            Logger.getLogger("RabbitMQ").log(Level.INFO, "RabbitMQ Emit " + message.type)
        } catch (e: Exception) {
            Logger.getLogger("RabbitMQ").log(Level.SEVERE, "RabbitMQ no se pudo encolar " + message.type, e)
        }
    }
}