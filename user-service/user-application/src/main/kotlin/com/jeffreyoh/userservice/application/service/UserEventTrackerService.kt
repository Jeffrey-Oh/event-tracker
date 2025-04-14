package com.jeffreyoh.userservice.application.service

import com.jeffreyoh.userservice.application.model.event.EventTrackerCommand
import com.jeffreyoh.userservice.application.port.`in`.EventTrackerUseCase
import com.jeffreyoh.userservice.application.port.out.EventTrackerPort
import reactor.core.publisher.Mono

class UserEventTrackerService(
    private val eventTrackerPort: EventTrackerPort
): EventTrackerUseCase {

    override fun sendEvent(command: EventTrackerCommand.SaveEvent): Mono<Void> {
        return eventTrackerPort.sendEvent(command.toRequest())
    }

}