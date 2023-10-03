package utils.db

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import utils.env.Environment.env

/**
 * Permite la configuración del acceso a la db
 */
class MongoStore {
    private var pojoCodecRegistry: CodecRegistry =
        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
    private var codecRegistry: CodecRegistry =
        CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry)

    private var clientSettings: MongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(env.databaseUrl))
        .codecRegistry(codecRegistry)
        .build()

    private val client = MongoClient.create(clientSettings)
    val database = client.getDatabase(databaseName = "orders_kotlin")

    inline fun <reified T : Any> collection(collectionName: String): MongoCollection<T> {
        return database.getCollection<T>(collectionName = collectionName)
    }
}
