package security

import security.dao.User
import utils.errors.UnauthorizedError

fun TokenService.validateUserIsAdmin(token: String): User {
    val cachedUser = token.validateTokenIsLoggedIn(this)

    if (cachedUser.permissions?.contains("admin") != true) {
        throw UnauthorizedError()
    }

    return cachedUser
}

fun String?.validateTokenIsAdminUser(tokenService: TokenService): User {
    this ?: throw UnauthorizedError()
    return tokenService.validateUserIsAdmin(this)
}
