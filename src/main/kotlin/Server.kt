import batch.batchModule
import events.eventsModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import projections.projectionsModule
import rabbit.Consumers
import rabbit.rabbitModule
import rest.Routes
import rest.routesModule
import security.securityModule
import utils.db.databaseModule
import utils.env.Environment
import utils.env.Log

fun main() {
    Server().start()
}

class Server : KoinComponent {
    private val routes: Routes by inject()
    private val consumers: Consumers by inject()

    fun start() {
        startKoin {
            modules(
                routesModule, databaseModule, rabbitModule,
                securityModule, projectionsModule, eventsModule,
                batchModule
            )
        }

        Log.info("Order Service escuchando en el puerto : ${Environment.env.serverPort}")

        consumers.init()
        routes.init()
    }
}