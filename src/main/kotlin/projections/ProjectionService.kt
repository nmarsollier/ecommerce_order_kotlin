package projections

import events.repository.Event
import projections.orderStatus.OrderStatusService

class ProjectionService(
    val service: OrderStatusService
) {
    fun updateProjections(event: Event) {
        Thread { service.update(event) }.start()
    }
}