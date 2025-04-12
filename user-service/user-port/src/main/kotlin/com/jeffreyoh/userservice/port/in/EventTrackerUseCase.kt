package com.jeffreyoh.userservice.port.`in`

import com.jeffreyoh.userservice.core.command.EventTrackerCommand
import reactor.core.publisher.Mono

interface EventTrackerUseCase {

    fun sendEvent(command: EventTrackerCommand.SaveEventCommand): Mono<Void>

}