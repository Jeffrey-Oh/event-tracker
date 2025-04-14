package com.jeffreyoh.eventtracker.api.adapter.inbound.web.controller

import com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto.EventStatisticsDTO
import com.jeffreyoh.eventtracker.application.port.`in`.GetEventStatisticsUseCase
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@Validated
@RestController
@RequestMapping("/api/statistics")
class StatisticsController(
    private val getEventStatisticsUseCase: GetEventStatisticsUseCase
) {

    @GetMapping
    fun getCount(
        @Valid request: EventStatisticsDTO.GetStatisticsRequest
    ): Mono<EventStatisticsDTO.EventStatisticsResponse> {
        return getEventStatisticsUseCase.getCount(request.toCommand())
            .map { count -> EventStatisticsDTO.EventStatisticsResponse(count) }
    }

}