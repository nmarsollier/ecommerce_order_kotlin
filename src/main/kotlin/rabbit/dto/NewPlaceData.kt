package rabbit.dto

import com.google.gson.annotations.SerializedName
import utils.validator.*

data class NewPlaceData(
    @SerializedName("cartId")
    @Required
    @MinLen(1)
    val cartId: String? = null,

    @SerializedName("userId")
    @Required
    @MinLen(1)
    val userId: String? = null,

    @SerializedName("articles")
    @Required
    @NotEmpty
    @Validate
    val articles: Array<Article>
) {
    data class Article(
        @SerializedName("id")
        @Required
        @MinLen(1)
        val id: String? = null,

        @SerializedName("quantity")
        @Required
        @MinValue(1)
        val quantity: Int = 0
    )
}