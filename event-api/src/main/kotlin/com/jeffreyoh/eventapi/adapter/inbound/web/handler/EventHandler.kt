package com.jeffreyoh.eventapi.adapter.inbound.web.handler

import com.jeffreyoh.eventapi.adapter.inbound.web.dto.SaveEventRequest
import com.jeffreyoh.eventport.input.SaveEventUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Component
class EventHandler(
    private val saveEventUseCase: SaveEventUseCase
) {

    fun saveEvent(request: SaveEventRequest): Mono<Void> {
        log.debug { "Handling event at handler level: $request" }
        return saveEventUseCase.saveEvent(request.toCommand())
    }

}