package utils.env

import com.google.gson.annotations.SerializedName

data class EnvData(
    @SerializedName("serverPort")
    val serverPort: Int,

    @SerializedName("securityServerUrl")
    val securityServerUrl: String,

    @SerializedName("rabbitServerUrl")
    val rabbitServerUrl: String,

    @SerializedName("databaseUrl")
    val databaseUrl: String,

    @SerializedName("staticLocation")
    val staticLocation: String
) {}

object Environment {
    var env = EnvData(
        serverPort = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 3004,
        securityServerUrl = System.getenv("AUTH_SERVICE_URL") ?: "http://localhost:3000",
        rabbitServerUrl = System.getenv("RABBIT_URL") ?: "localhost",
        databaseUrl = System.getenv("MONGO_URL") ?: "mongodb://localhost",
        staticLocation = System.getenv("WWW_PATH") ?: "${System.getProperty("user.dir")}/resources/www"
    )
}