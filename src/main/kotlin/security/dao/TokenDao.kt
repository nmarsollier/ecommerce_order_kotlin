package security.dao

import org.apache.http.util.EntityUtils
import utils.env.Environment
import utils.env.Log
import utils.gson.jsonToObject
import utils.http.HttpTools

class TokenDao(
    private val http: HttpTools
) {
    fun retrieveUser(token: String): User? {
        return try {
            http.get(
                "${Environment.env.securityServerUrl}/v1/users/current",
                listOf("Authorization" to token)
            ).let {
                if (it.statusLine.statusCode != 200) {
                    return null
                }

                val entity = it.entity ?: return null
                val entityString = EntityUtils.toString(entity)

                entityString.jsonToObject<User>()
            }
        } catch (e: Exception) {
            Log.error(e)
            null
        }
    }
}