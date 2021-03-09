import rabbit.Consumers
import rest.Routes
import utils.db.OrderStore
import utils.db.ProjectionsStore
import utils.env.Environment
import utils.env.Log

fun main() {
    Server().start()
}

class Server {
    fun start() {
        Log.info("Order Service escuchando en el puerto : ${Environment.env.serverPort}")

        // Initialize Mongo Store
        OrderStore.instance()
        ProjectionsStore.instance()
        Routes.init()
        Consumers.init()
    }
}