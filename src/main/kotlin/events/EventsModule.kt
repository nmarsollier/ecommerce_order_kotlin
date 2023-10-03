package events

import events.repository.EventRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val eventsModule = module {
    singleOf(::EventService)
    singleOf(::EventRepository)
}