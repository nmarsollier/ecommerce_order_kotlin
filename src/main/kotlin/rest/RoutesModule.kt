package rest

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val routesModule = module {
    singleOf(::GetOrders)
    singleOf(::GetOrdersBatchPaymentDefined)
    singleOf(::GetOrdersBatchPlaced)
    singleOf(::GetOrdersBatchValidated)
    singleOf(::GetOrdersId)
    singleOf(::PostOrdersIdPayment)
    singleOf(::ErrorHandler)
    singleOf(::Routes)
}