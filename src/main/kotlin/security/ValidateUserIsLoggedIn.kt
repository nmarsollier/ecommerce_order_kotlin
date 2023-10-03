package security

import security.dao.User
import utils.errors.UnauthorizedError

fun TokenService.validateUserIsLoggedIn(token: String): User {
    if (token.isBlank()) {
        throw UnauthorizedError()
    }

    return getUserByToken(token)
}

fun String?.validateTokenIsLoggedIn(tokenService: TokenService): User {
    this ?: throw UnauthorizedError()
    return tokenService.validateUserIsLoggedIn(this)
}
