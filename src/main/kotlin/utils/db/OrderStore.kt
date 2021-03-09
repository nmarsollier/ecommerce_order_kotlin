package utils.db

import com.google.common.annotations.VisibleForTesting
import com.mongodb.MongoClient
import org.bson.types.ObjectId
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Key
import org.mongodb.morphia.Morphia
import org.mongodb.morphia.query.Query
import utils.env.Environment.env
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Permite la configuración del acceso a la db
 */
class OrderStore private constructor(
    var _dataStore: Datastore? = null
) {

    init {
        Logger.getLogger("org.mongodb.driver").level = Level.SEVERE
        Logger.getLogger("org.mongodb.morphia").level = Level.SEVERE

        val morphia = Morphia()
        val client = MongoClient(env.databaseUrl)
        if (_dataStore == null) {
            _dataStore = morphia.createDatastore(client, "order_kotlin").also {
                it.ensureIndexes()
            }
        }
    }

    fun <T> save(entity: T): Key<T> {
        return _dataStore!!.save(entity)
    }

    inline fun <reified T> findById(id: String): T {
        return _dataStore!![T::class.java, ObjectId(id)]
    }

    inline fun <reified T> createQuery(): Query<T>? {
        return _dataStore!!.createQuery(T::class.java)
    }

    companion object {
        private var singleInstance: OrderStore? = null

        fun instance(): OrderStore {
            return singleInstance ?: OrderStore().also {
                singleInstance = it
            }
        }

        @VisibleForTesting
        fun mockedInstance(store: Datastore): OrderStore {
            return OrderStore(store)
        }
    }
}