package model.orders.events.dto

import com.google.gson.annotations.SerializedName
import model.orders.events.repository.PaymentEvent
import utils.validator.MinLen
import utils.validator.MinValue
import utils.validator.Required

data class PaymentData(
    @SerializedName("orderId")
    var orderId: String? = null,

    @SerializedName("userId")
    @Required
    @MinLen(1)
    var userId: String? = null,

    @SerializedName("method")
    @Required
    var method: PaymentEvent.Method? = null,

    @SerializedName("amount")
    @Required
    @MinValue(0)
    var amount: Double = 0.0
)