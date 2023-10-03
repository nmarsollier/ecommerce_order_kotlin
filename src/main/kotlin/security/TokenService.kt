package security

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import security.dao.TokenDao
import security.dao.User
import utils.errors.UnauthorizedError
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

/**
 * @apiDefine AuthHeader
 *
 * @apiExample {String} Header Autorización
 * Authorization=bearer {token}
 *
 * @apiErrorExample 401 Unauthorized
 * HTTP/1.1 401 Unauthorized
 */
class TokenService(
    private var dao: TokenDao
) {
    private val map = CacheBuilder
        .newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(60, TimeUnit.MINUTES)
        .build(object : CacheLoader<String, User>() {
            override fun load(key: String): User {
                return dao.retrieveUser(key) ?: throw UnauthorizedError()
            }
        })

    /*
    Get a valid user or throws UnauthorizedError
     */
    fun getUserByToken(token: String): User {
        try {
            return map[token]
        } catch (e: ExecutionException) {
            throw e.cause as Exception
        } catch (e: Exception) {
            throw e
        }
    }

    fun invalidateTokenCache(token: String) {
        map.invalidate(token)
    }
}