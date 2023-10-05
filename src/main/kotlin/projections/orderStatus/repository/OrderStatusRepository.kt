package projections.orderStatus.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.types.ObjectId
import projections.order.repository.Status
import utils.db.MongoStore

class OrderStatusRepository(
    private val mongoStore: MongoStore
) {
    private val collection = mongoStore.collection<OrderStatus>("orders_projection")

    /**
     * Devuelve una orden especifica
     */
    suspend fun findById(id: String): OrderStatus? {
        return collection.find(Filters.eq("_id", ObjectId(id))).firstOrNull()
    }

    /**
     * Devuelve las ordenes de un usuario especifico
     */
    suspend fun findByUserId(userId: String?): List<OrderStatus> {
        return collection
            .find(
                Filters.eq("userId", userId)
            )
            .sort(Sorts.ascending("created"))
            .toList()
    }

    /**
     * Devuelve las ordenes que tienen un estado en particular
     */
    suspend fun findByStatus(status: Status): List<OrderStatus> {
        return collection
            .find(
                Filters.eq("status", status)
            )
            .sort(Sorts.ascending("created"))
            .toList()
    }

    suspend fun save(order: OrderStatus) {
        if (order.id != null) {
            collection.replaceOne(Filters.eq("_id", ObjectId(order.id)), order)
        } else {
            collection.insertOne(order)
        }
    }

    suspend fun delete(orderId: String) {
        collection.deleteOne(Filters.eq("_id", ObjectId(orderId)))
    }
}

suspend fun OrderStatus.saveIn(repository: OrderStatusRepository): OrderStatus {
    repository.save(this)
    return this
}