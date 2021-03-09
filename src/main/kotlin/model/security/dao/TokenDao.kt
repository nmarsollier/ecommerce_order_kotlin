package model.security.dao

import org.apache.http.util.EntityUtils
import utils.env.Environment
import utils.env.Log
import utils.gson.jsonToObject
import utils.http.HttpTools

class TokenDao private constructor(
    val http: HttpTools = HttpTools.instance()
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

                it.entity ?: return null

                EntityUtils.toString(it.entity).jsonToObject<User>()
            }
        } catch (e: Exception) {
            Log.error(e)
            null
        }
    }

    companion object {
        private var currentInstance: TokenDao? = null

        fun instance(): TokenDao {
            return currentInstance ?: TokenDao().also {
                currentInstance = it
            }
        }
    }
}