package utils.errors

import com.google.gson.annotations.SerializedName

/**
 * Un error de validaciones de atributos de una clase.
 * Estos errores se pueden serializar como Json.
 */
open class ValidationError(
    vararg errors: Pair<String, String>
) : Exception() {
    private var messages: MutableList<ValidationMessage> = mutableListOf()

    init {
        errors.forEach {
            addPath(it.first, it.second)
        }
    }

    val isEmpty: Boolean
        get() = messages.size == 0

    fun addPath(path: String?, message: String?): ValidationError {
        messages.add(ValidationMessage(path, message))
        return this
    }

    fun json() = SerializedMessage(messages)

    data class ValidationMessage(
        @SerializedName("path")
        var path: String?,
        @SerializedName("message")
        var message: String?
    )

    data class SerializedMessage(
        @SerializedName("messages")
        var messages: MutableList<ValidationMessage> = mutableListOf()
    )
}