package model.orders.projections.orderStatus.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import model.db.MongoStore
import model.orders.projections.order.repository.Status
import org.bson.types.ObjectId

class OrderStatusRepository private constructor() {
    private val collection = MongoStore.collection<OrderStatus>("orders_projection")

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
        collection.insertOne(order)
    }

    suspend fun delete(orderId: String) {
        collection.deleteOne(Filters.eq("_id", ObjectId(orderId)))
    }

    companion object {
        private var currentInstance: OrderStatusRepository? = null

        fun instance(): OrderStatusRepository {
            return currentInstance ?: OrderStatusRepository().also {
                currentInstance = it
            }
        }
    }
}