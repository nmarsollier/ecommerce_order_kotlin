package security

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import security.dao.TokenDao
import utils.http.HttpTools

val securityModule = module {
    singleOf(::TokenService)
    singleOf(::TokenDao)
    singleOf(::HttpTools)
}
