package com.jeffreyoh.userservice.application.port.`in`

import com.jeffreyoh.userservice.application.model.event.EventTrackerCommand
import reactor.core.publisher.Mono

interface EventTrackerUseCase {

    fun sendEvent(command: EventTrackerCommand.SaveEvent): Mono<Void>

}