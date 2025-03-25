package com.jeffreyoh.eventapi.adapter.inbound.web.controller

import com.jeffreyoh.eventapi.adapter.inbound.web.dto.SaveEventRequest
import com.jeffreyoh.eventapi.adapter.inbound.web.handler.EventHandler
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventHandler: EventHandler
) {

    @PostMapping
    fun receiveEvent(
        @RequestBody request: SaveEventRequest
    ): Mono<ResponseEntity<Void>> {
        log.debug { "Received API request: $request" }
        return eventHandler.saveEvent(request)
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build())
    }

}