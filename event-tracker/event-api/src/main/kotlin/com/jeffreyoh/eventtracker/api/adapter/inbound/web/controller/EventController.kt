package com.jeffreyoh.eventtracker.api.adapter.inbound.web.controller

import com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto.SaveEventDTO
import com.jeffreyoh.eventtracker.application.port.`in`.SaveEventUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/events")
class EventController(
    private val saveEventUseCase: SaveEventUseCase
) {

    @PostMapping
    fun receiveEvent(
        @RequestBody @Valid request: SaveEventDTO.SaveEventRequest
    ): Mono<ResponseEntity<Void>> {
        val runnable: Mono<Void> = Mono.fromRunnable {
            saveEventUseCase.saveEvent(request.toCommand())
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError { log.warn { "❌ 이벤트 저장 실패" } }
                .subscribe()
        }

        return runnable.thenReturn(ResponseEntity.status(HttpStatus.CREATED).build())
    }

}