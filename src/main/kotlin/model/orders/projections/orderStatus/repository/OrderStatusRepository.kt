package model.orders.projections.orderStatus.repository

import model.orders.projections.order.repository.Status
import org.bson.types.ObjectId
import utils.db.ProjectionsStore

class OrderStatusRepository private constructor(
    val store: ProjectionsStore = ProjectionsStore.instance()
) {
    /**
     * Devuelve una orden especifica
     */
    fun findById(id: ObjectId): OrderStatus {
        return store.findById(id)
    }

    /**
     * Devuelve las ordenes de un usuario especifico
     */
    fun findByUserId(userId: String?): List<OrderStatus> {
        return store.createQuery<OrderStatus>()?.let {
            it.and(it.criteria("userId").equal(userId))
            it.order("created")
            it.fetch().toList()
        } ?: emptyList()
    }

    /**
     * Devuelve las ordenes que tienen un estado en particular
     */
    fun findByStatus(status: Status): List<OrderStatus> {
        return store.createQuery<OrderStatus>()?.let {
            it.and(it.criteria("status").equal(status))
            it.order("created")
            it.fetch().toList()
        } ?: emptyList()
    }

    fun save(order: OrderStatus) {
        store.save(order)
    }

    fun delete(orderId: ObjectId) {
        store.delete<OrderStatus>(orderId)
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