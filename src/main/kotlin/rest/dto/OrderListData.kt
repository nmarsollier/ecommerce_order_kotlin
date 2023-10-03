package rest.dto

import com.google.gson.annotations.SerializedName
import projections.order.repository.Status
import projections.orderStatus.repository.OrderStatus

class OrderListData constructor(order: OrderStatus) {
    @SerializedName("id")
    var id: String = order.id!!

    @SerializedName("status")
    var status: Status = order.status ?: Status.INVALID

    @SerializedName("cartId")
    var cartId: String? = order.cartId

    @SerializedName("totalPrice")
    var totalPrice: Double = order.totalPrice

    @SerializedName("totalPayment")
    var totalPayment: Double = order.payment
    var updated = order.updated
    var created = order.created

    @SerializedName("articles")
    var articles = order.articles
}