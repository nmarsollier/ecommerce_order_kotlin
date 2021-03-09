package utils.rabbit

import com.google.gson.annotations.SerializedName
import utils.validator.Required

data class RabbitEvent(
    // tipo de mensaje enviado
    @SerializedName("type")
    @Required
    var type: String? = null,

    // Version del protocolo
    @SerializedName("version")
    var version: Int = 0,

    // Por si el destinatario necesita saber de donde viene el mensaje
    @SerializedName("queue")
    var queue: String? = null,

    // Por si el destinatario necesita saber de donde viene el mensaje
    @SerializedName("exchange")
    var exchange: String? = null,

    // El body del mensaje
    @SerializedName("message")
    @Required
    var message: Any? = null
)