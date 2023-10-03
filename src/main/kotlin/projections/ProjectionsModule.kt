package projections

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import projections.order.OrderService
import projections.orderStatus.OrderStatusService
import projections.orderStatus.repository.OrderStatusRepository

val projectionsModule = module {
    singleOf(::ProjectionService)
    singleOf(::OrderStatusService)
    singleOf(::OrderStatusRepository)
    singleOf(::OrderService)
}