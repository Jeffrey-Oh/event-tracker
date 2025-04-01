package com.jeffreyoh.eventapi.adapter.inbound.web.controller

import com.jeffreyoh.eventapi.adapter.inbound.web.dto.EventStatisticsDTO
import com.jeffreyoh.eventapi.adapter.inbound.web.dto.toEventTypeOrThrow
import com.jeffreyoh.eventapi.adapter.inbound.web.handler.StatisticsHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/statistics")
class StatisticsController(
    private val statisticsHandler: StatisticsHandler
) {

    @GetMapping("/{eventType}/{componentId}")
    fun getClickCount(
        @PathVariable eventType: String,
        @PathVariable componentId: Long
    ): Mono<EventStatisticsDTO.EventStatisticsResponse> {
        return statisticsHandler.getClickCount(componentId, eventType.toEventTypeOrThrow())
    }

}