package com.jeffreyoh.eventapi.adapter.inbound.web.controller

import com.jeffreyoh.eventapi.adapter.inbound.web.dto.EventStatisticDTO
import com.jeffreyoh.eventapi.adapter.inbound.web.dto.toEventTypeOrThrow
import com.jeffreyoh.eventapi.adapter.inbound.web.handler.StatisticHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/statistics")
class StatisticController(
    private val statisticHandler: StatisticHandler
) {

    @GetMapping("/{eventType}/{componentId}")
    fun getClickCount(
        @PathVariable eventType: String,
        @PathVariable componentId: Long
    ): Mono<EventStatisticDTO.EventStatisticResponse> {
        return statisticHandler.getClickCount(componentId, eventType.toEventTypeOrThrow())
    }

}