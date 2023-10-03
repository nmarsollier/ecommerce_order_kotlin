package security

import utils.errors.UnauthorizedError

fun TokenService.validateAdminUser(token: String) {
    validateUser(token)

    val cachedUser = getUserByToken(token)

    if (cachedUser.permissions?.contains("admin") != true) {
        throw UnauthorizedError()
    }
}