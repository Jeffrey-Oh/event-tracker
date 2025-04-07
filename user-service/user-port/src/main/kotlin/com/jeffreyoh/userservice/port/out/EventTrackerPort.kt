package com.jeffreyoh.userservice.port.out

import com.jeffreyoh.userservice.core.domain.EventTrackerCommand
import reactor.core.publisher.Mono

interface EventTrackerPort {

    fun sendEvent(command: EventTrackerCommand.PayloadCommand): Mono<Void>
    fun sendSearchEvent(command: EventTrackerCommand.SearchCommand): Mono<Void>

}