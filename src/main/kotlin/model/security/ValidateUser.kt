package model.security

import utils.errors.UnauthorizedError

fun TokenService.validateUser(token: String) {
    if (token.isBlank()) {
        throw UnauthorizedError()
    }

    getUserByToken(token)
}