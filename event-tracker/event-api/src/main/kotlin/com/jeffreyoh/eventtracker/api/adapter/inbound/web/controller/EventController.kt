package com.jeffreyoh.eventtracker.api.adapter.inbound.web.controller

import com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto.SaveEventDTO
import com.jeffreyoh.eventtracker.port.input.SaveEventUseCase
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/events")
class EventController(
    private val saveEventUseCase: SaveEventUseCase
) {

    @PostMapping
    fun receiveEvent(
        @RequestBody @Valid request: SaveEventDTO.SaveEventRequest
    ): Mono<ResponseEntity<Void>> {
        return saveEventUseCase.saveEvent(request.toCommand())
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build())
    }

}