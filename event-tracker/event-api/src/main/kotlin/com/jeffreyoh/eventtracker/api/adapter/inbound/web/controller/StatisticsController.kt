package com.jeffreyoh.eventtracker.api.adapter.inbound.web.controller

import com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto.EventStatisticsDTO
import com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto.toEventTypeOrThrow
import com.jeffreyoh.eventtracker.port.input.GetEventStatisticsUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/statistics")
class StatisticsController(
    private val getEventStatisticsUseCase: GetEventStatisticsUseCase
) {

    @GetMapping("/{eventType}/{componentId}")
    fun getCount(
        @PathVariable eventType: String,
        @PathVariable componentId: Long
    ): Mono<EventStatisticsDTO.EventStatisticsResponse> {
        return getEventStatisticsUseCase.getCount(componentId, eventType.toEventTypeOrThrow())
            .map { count -> toResponse(componentId, count) }
    }

    @GetMapping("/like/{componentId}/{postId}")
    fun getLikeCount(
        @PathVariable componentId: Long,
        @PathVariable postId: Long
    ): Mono<EventStatisticsDTO.EventStatisticsResponse> {
        return getEventStatisticsUseCase.getLikeCount(componentId, postId)
            .map { count -> toResponse(componentId, count) }
    }

    private fun toResponse(
        componentId: Long,
        count: Long
    ): EventStatisticsDTO.EventStatisticsResponse {
        return EventStatisticsDTO.EventStatisticsResponse(
            componentId,
            count
        )
    }

}