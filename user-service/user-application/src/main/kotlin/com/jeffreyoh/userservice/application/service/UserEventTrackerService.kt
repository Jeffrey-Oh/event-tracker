package com.jeffreyoh.userservice.application.service

import com.jeffreyoh.userservice.core.command.EventTrackerCommand
import com.jeffreyoh.userservice.port.`in`.EventTrackerUseCase
import com.jeffreyoh.userservice.port.out.EventTrackerPort
import reactor.core.publisher.Mono

class UserEventTrackerService(
    private val eventTrackerPort: EventTrackerPort
): EventTrackerUseCase {

    override fun sendEvent(command: EventTrackerCommand.SaveEventCommand): Mono<Void> {
        return eventTrackerPort.sendEvent(command)
    }

}