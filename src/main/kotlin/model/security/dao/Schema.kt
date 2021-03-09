package model.security.dao

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("login")
    var login: String? = null,

    @SerializedName("permissions")
    var permissions: Array<String>? = null
)