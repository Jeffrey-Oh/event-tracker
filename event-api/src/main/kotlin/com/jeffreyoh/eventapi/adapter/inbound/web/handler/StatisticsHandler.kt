package com.jeffreyoh.eventapi.adapter.inbound.web.handler

import com.jeffreyoh.eventapi.adapter.inbound.web.dto.EventStatisticsDTO
import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.input.GetEventStatisticsUseCase
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatisticsHandler(
    private val getEventStatisticsUseCase: GetEventStatisticsUseCase
) {

    fun getClickCount(componentId: Long, eventType: EventType): Mono<EventStatisticsDTO.EventStatisticsResponse> {
        return getEventStatisticsUseCase.getCount(componentId, eventType)
            .map {
                count ->
                EventStatisticsDTO.EventStatisticsResponse(
                    componentId,
                    count
                )
            }
    }

}