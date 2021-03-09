package model.orders.events.repository

import org.bson.types.ObjectId
import utils.db.OrderStore
import utils.kt.nextOrNull

class EventRepository private constructor(
    private var store: OrderStore = OrderStore.instance()
) {
    fun save(event: Event): Event {
        store.save(event)
        return event
    }

    fun findPlaceByCartId(cartId: String?): Event? {
        return store.createQuery<Event>()?.let {
            it.and(
                it.criteria("type").equal(EventType.PLACE_ORDER),
                it.criteria("placeEvent.cartId").equal(cartId)
            )

            return it.fetch().nextOrNull()
        }
    }


    fun findPlaceByOrderId(orderId: ObjectId?): Event? {
        return store.createQuery<Event>()?.let {
            it.and(
                it.criteria("type").equal(EventType.PLACE_ORDER),
                it.criteria("orderId").equal(orderId)
            )
            return it.fetch().nextOrNull()
        }
    }

    fun findByOrderId(orderId: ObjectId?): List<Event> {
        return store.createQuery<Event>()?.let {
            it.and(it.criteria("orderId").equal(orderId))
            it.order("created")

            it.fetch().toList()
        } ?: emptyList()
    }

    companion object {
        private var currentInstance: EventRepository? = null

        fun instance(): EventRepository {
            return currentInstance ?: EventRepository().also {
                currentInstance = it
            }
        }
    }
}