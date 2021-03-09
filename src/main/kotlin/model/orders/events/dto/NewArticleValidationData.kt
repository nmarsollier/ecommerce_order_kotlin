package model.orders.events.dto

import com.google.gson.annotations.SerializedName
import utils.validator.MinLen
import utils.validator.Required

data class NewArticleValidationData(
    @SerializedName("referenceId")
    @Required
    @MinLen(1)
    val orderId: String? = null,

    @SerializedName("articleId")
    @Required
    @MinLen(1)
    val articleId: String? = null,

    @SerializedName("valid")
    val valid: Boolean = false,

    @SerializedName("stock")
    val stock: Int = 0,

    @SerializedName("price")
    val price: Double = 0.0
)