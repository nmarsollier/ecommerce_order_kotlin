package model.orders.projections

import model.orders.events.repository.Event
import model.orders.projections.orderStatus.OrderStatusService


class ProjectionService private constructor(
    val service: OrderStatusService = OrderStatusService.instance()
) {
    fun updateProjections(event: Event) {
        Thread { service.update(event) }.start()
    }

    companion object {
        private var currentInstance: ProjectionService? = null

        fun instance(): ProjectionService {
            return currentInstance ?: ProjectionService().also {
                currentInstance = it
            }
        }
    }
}