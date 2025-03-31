package com.jeffreyoh.eventapi.adapter.inbound.web.handler

import com.jeffreyoh.eventapi.adapter.inbound.web.dto.EventStatisticDTO
import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.input.GetEventStatisticUseCase
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatisticHandler(
    private val getEventStatisticUseCase: GetEventStatisticUseCase
) {

    fun getClickCount(componentId: Long, eventType: EventType): Mono<EventStatisticDTO.EventStatisticResponse> {
        return getEventStatisticUseCase.getCount(componentId, eventType)
            .map {
                count ->
                EventStatisticDTO.EventStatisticResponse(
                    componentId,
                    count
                )
            }
    }

}