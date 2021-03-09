package model.orders.projections.order.repository

import model.orders.events.repository.Event
import model.orders.events.repository.EventType
import model.orders.projections.order.repository.Order.Payment
import java.util.*

typealias OrderUpdater = (order: Order, event: Event) -> Order

internal fun Order.getUpdaterForEvent(type: EventType?): OrderUpdater {
    return when (type) {
        EventType.PLACE_ORDER -> placeEventUpdater
        EventType.ARTICLE_VALIDATION -> articleValidationUpdater
        EventType.PAYMENT -> paymentUpdater
        else -> voidEventUpdater
    }
}

internal val placeEventUpdater = { order: Order, event: Event ->
    order.copy(
        id = event.orderId,
        userId = event.placeEvent?.userId,
        cartId = event.placeEvent?.cartId,
        status = Status.PLACED,
        articles = event.placeEvent?.articles?.map {
            Order.Article(it.articleId, it.quantity)
        } ?: emptyList(),
        updated = Date()
    )
}

internal val articleValidationUpdater = { order: Order, event: Event ->
    val newOrder = order.copy()

    newOrder.articles
        .filter {
            it.id == event.articleValidationEvent?.articleId
        }
        .forEach {
            it.isValid = event.articleValidationEvent!!.isValid
            it.unitaryPrice = event.articleValidationEvent!!.price
            it.isValidated = true
        }

    var status = Status.INVALID
    if (order.articles.firstOrNull { !it.isValid } == null) {
        Status.VALIDATED
    }

    newOrder.copy(
        status = status,
        updated = Date()
    )
}


internal val paymentUpdater = { order: Order, event: Event ->
    val orders = order.payment.toMutableList()
    event.payment?.let {
        orders.add(
            Payment(
                method = it.method,
                amount = it.amount
            )
        )
    }

    val status = if (order.totalPayment >= order.totalPrice) {
        Status.PAYMENT_DEFINED
    } else {
        order.status
    }

    order.copy(
        payment = orders,
        status = status
    )
}

internal val voidEventUpdater = { order: Order, _: Event ->
    order
}
