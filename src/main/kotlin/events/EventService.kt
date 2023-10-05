package events

import events.repository.*
import projections.ProjectionService
import projections.updateProjections
import rabbit.dto.NewArticleValidationData
import rabbit.dto.NewPlaceData
import rabbit.dto.PaymentData
import utils.validator.validate

class EventService(
    val repository: EventRepository,
    val projections: ProjectionService
) {
    suspend fun placeOrder(data: NewPlaceData): Event {
        data.validate()
        return repository.findPlaceByCartId(data.cartId) ?: let {
            Event.newPlaceOrder(data.toPlaceEvent())
                .saveIn(repository)
                .updateProjections(projections)
        }
    }

    suspend fun placeArticleExist(data: NewArticleValidationData): Event {
        data.validate()

        val event = Event.newArticleValidation(
            data.orderId,
            ArticleValidationEvent(data.articleId, data.valid, data.stock, data.price)
        )

        return event.saveIn(repository).updateProjections(projections)
    }

    suspend fun placePayment(payment: PaymentData): Event {
        payment.validate()

        val event = Event.newPayment(payment.orderId, payment.userId, payment.method, payment.amount)

        return event.saveIn(repository).updateProjections(projections)
    }
}

private fun NewPlaceData.toPlaceEvent() = PlaceEvent(
    this.cartId,
    this.userId,
    this.articles.map {
        PlaceEvent.Article(it.id, it.quantity)
    }
)