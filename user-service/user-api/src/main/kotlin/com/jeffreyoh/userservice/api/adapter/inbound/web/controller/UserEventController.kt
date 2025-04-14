package com.jeffreyoh.userservice.api.adapter.inbound.web.controller

import com.jeffreyoh.userservice.api.adapter.inbound.web.dto.UserEventTrackerDTO
import com.jeffreyoh.userservice.application.port.`in`.EventTrackerUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/users/events")
class UserEventController(
    private val eventTrackerUseCase: EventTrackerUseCase
) {

    @PostMapping
    fun userSendEvent(
        @RequestBody request: UserEventTrackerDTO.SaveEventRequest
    ): Mono<Void> {
        return eventTrackerUseCase.sendEvent(request.toCommand())
    }

}