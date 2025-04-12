package com.jeffreyoh.userservice.port.out

import com.jeffreyoh.userservice.core.command.EventTrackerCommand
import reactor.core.publisher.Mono

interface EventTrackerPort {

    fun sendEvent(command: EventTrackerCommand.SaveEventCommand): Mono<Void>

}