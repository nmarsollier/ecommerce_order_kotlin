package model.orders.events

import model.orders.events.dto.NewArticleValidationData
import model.orders.events.dto.NewPlaceData
import model.orders.events.dto.PaymentData
import model.orders.events.repository.ArticleValidationEvent
import model.orders.events.repository.Event
import model.orders.events.repository.EventRepository
import model.orders.events.repository.PlaceEvent
import model.orders.projections.ProjectionService
import utils.validator.validate

class EventService private constructor(
    val repository: EventRepository = EventRepository.instance(),
    val projections: ProjectionService = ProjectionService.instance()
) {
    fun placeOrder(data: NewPlaceData): Event {
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
            repository.save(event)
            projections.updateProjections(event)
            event
        }
    }

    fun placeArticleExist(data: NewArticleValidationData): Event {
        data.validate()

        val event = Event.newArticleValidation(
            data.orderId,
            ArticleValidationEvent(data.articleId, data.valid, data.stock, data.price)
        )

        repository.save(event)

        projections.updateProjections(event)

        return event
    }

    fun placePayment(payment: PaymentData): Event {
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