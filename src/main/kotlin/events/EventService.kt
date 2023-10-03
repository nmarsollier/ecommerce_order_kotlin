package events

import rabbit.NewArticleValidationData
import rabbit.NewPlaceData
import rabbit.PaymentData
import events.repository.ArticleValidationEvent
import events.repository.Event
import events.repository.EventRepository
import events.repository.PlaceEvent
import projections.ProjectionService
import utils.validator.validate

class EventService private constructor(
    val repository: EventRepository = EventRepository.instance(),
    val projections: ProjectionService = ProjectionService.instance()
) {
    suspend fun placeOrder(data: NewPlaceData): Event {
        data.validate()
        return repository.findPlaceByCartId(data.cartId) ?: let {
            val event = Event.newPlaceOrder(
                PlaceEvent(
                    data.cartId,
                    data.userId,
                    data.articles.map {
                        PlaceEvent.Article(it.id, it.quantity)
                    }
                )
            )
            repository.save(event).let {
                projections.updateProjections(it)
                it
            }
        }
    }

    suspend fun placeArticleExist(data: NewArticleValidationData): Event {
        data.validate()

        val event = Event.newArticleValidation(
            data.orderId,
            ArticleValidationEvent(data.articleId, data.valid, data.stock, data.price)
        )

        repository.save(event)

        projections.updateProjections(event)

        return event
    }

    suspend fun placePayment(payment: PaymentData): Event {
        payment.validate()

        val event = Event.newPayment(payment.orderId, payment.userId, payment.method, payment.amount)

        repository.save(event)
        projections.updateProjections(event)
        return event
    }

    companion object {
        private var currentInstance: EventService? = null

        fun instance(): EventService {
            return currentInstance ?: EventService().also {
                currentInstance = it
            }
        }
    }
}