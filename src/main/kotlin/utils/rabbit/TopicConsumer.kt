package utils.rabbit

import com.rabbitmq.client.*
import utils.env.Environment
import utils.gson.jsonToObject
import utils.validator.validate
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.collections.HashMap

/**
 * La cola topic permite que varios consumidores escuchen el mismo evento
 * topic es muy importante por es el evento que se va a escuchar
 * Para que un consumer escuche los eventos debe estar conectado al mismo exchange y escuchar el topic adecuado
 * queue permite distribuir la carga de los mensajes entre distintos consumers, los consumers con el mismo queue name
 * comparten la carga de procesamiento de mensajes, es importante que se defina el queue
 */
class TopicConsumer(private val exchange: String, private val queue: String, private val topic: String) {
    private val listeners: MutableMap<String?, EventProcessor> = HashMap()
    fun addProcessor(event: String?, listener: EventProcessor) {
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
            channel.exchangeDeclare(exchange, "topic")
            channel.queueDeclare(queue, false, false, false, null)
            channel.queueBind(queue, exchange, topic)
            Thread {
                try {
                    Logger.getLogger("RabbitMQ").log(Level.INFO, "RabbitMQ Topic Conectado")
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
                    channel.basicConsume(queue, true, consumer)
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