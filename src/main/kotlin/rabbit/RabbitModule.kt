package rabbit

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val rabbitModule = module {
    singleOf(::ConsumeArticleData)
    singleOf(::ConsumeAuthLogout)
    singleOf(::ConsumePlaceOrder)
    singleOf(::Consumers)
    singleOf(::EmitArticleValidation)
    singleOf(::EmitOrderPlaced)
}