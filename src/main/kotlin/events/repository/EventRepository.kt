package events.repository

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import utils.db.MongoStore

class EventRepository private constructor() {
    private val collection = MongoStore.collection<Event>("event")

    suspend fun save(event: Event): Event {
        val _id = collection.insertOne(event).insertedId?.toString()

        return event.copy(id = _id)
    }

    suspend fun findPlaceByCartId(cartId: String?): Event? {
        return collection.find(
            Filters.and(
                Filters.eq("placeEvent.cartId", cartId),
                Filters.eq("type", EventType.PLACE_ORDER),
            )
        ).firstOrNull()
    }


    suspend fun findPlaceByOrderId(orderId: String?): Event? {
        return collection.find(
            Filters.and(
                Filters.eq("orderId", orderId),
                Filters.eq("type", EventType.PLACE_ORDER),
            )
        ).firstOrNull()
    }

    suspend fun findByOrderId(orderId: String): List<Event> {
        return collection
                .find(
                    Filters.eq("orderId", orderId)
                )
                .sort(Sorts.ascending("created"))
                .toList()
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