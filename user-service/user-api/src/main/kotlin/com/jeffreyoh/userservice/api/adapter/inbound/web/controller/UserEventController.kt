package com.jeffreyoh.userservice.api.adapter.inbound.web.controller

import com.jeffreyoh.userservice.api.adapter.inbound.web.dto.UserEventTrackerDTO
import com.jeffreyoh.userservice.port.out.EventTrackerPort
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/events")
class EventProxyController(
    private val eventTrackerPort: EventTrackerPort
) {

    @PostMapping
    fun proxyEvent(
        @RequestBody request: UserEventTrackerDTO.SaveEventRequest
    ): Mono<Void> {
        return eventTrackerPort.sendEvent(request.toCommand())
    }

}