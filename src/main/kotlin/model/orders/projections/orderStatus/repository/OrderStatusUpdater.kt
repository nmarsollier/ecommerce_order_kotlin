package model.orders.projections.orderStatus.repository

import model.orders.events.repository.Event
import model.orders.events.repository.EventType
import model.orders.projections.order.repository.Status
import java.util.*

typealias OrderStatusUpdater = (order: OrderStatus, event: Event) -> OrderStatus

fun getUpdaterForEvent(type: EventType?): OrderStatusUpdater {
    when (type) {
        EventType.PLACE_ORDER -> placeEventUpdater
        EventType.ARTICLE_VALIDATION -> articleValidationUpdater
        EventType.PAYMENT -> paymentUpdater
    }
    return voidEventUpdater
}

internal val placeEventUpdater = { order: OrderStatus, event: Event ->
    order.copy(
        id = event.orderId,
        userId = event.placeEvent?.userId,
        cartId = event.placeEvent?.cartId,
        status = Status.PLACED,
        articles = event.placeEvent?.articles?.size ?: 0,
        updated = Date()
    )
}

internal val articleValidationUpdater = { order: OrderStatus, event: Event ->
    val stat = if (event.articleValidationEvent?.isValid != true) {
        Status.INVALID
    } else {
        order.status
    }

    order.copy(
        status = stat,
        updated = Date()
    )
}

internal val paymentUpdater = { order: OrderStatus, event: Event ->
    order.copy(
        payment = order.payment + (event.payment?.amount ?: 0.0)
    )
}

internal val voidEventUpdater = { order: OrderStatus, _: Event ->
    order
}
