package com.jeffreyoh.userservice.application.port.out

import com.jeffreyoh.userservice.application.model.event.EventTrackerRequest
import reactor.core.publisher.Mono

interface EventTrackerPort {

    fun sendEvent(request: EventTrackerRequest.SaveEvent): Mono<Void>

}