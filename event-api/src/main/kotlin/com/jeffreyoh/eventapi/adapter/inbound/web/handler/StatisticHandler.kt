package com.jeffreyoh.eventapi.adapter.inbound.web.handler

import com.jeffreyoh.eventapi.adapter.inbound.web.dto.ClickStatisticDTO
import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.input.GetClickStatisticUseCase
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatisticHandler(
    private val getClickStatisticUseCase: GetClickStatisticUseCase
) {

    fun getClickCount(componentId: Long, eventType: EventType): Mono<ClickStatisticDTO.ClistStatisticResponse> {
        return getClickStatisticUseCase.getCount(componentId, eventType)
            .map {
                count ->
                ClickStatisticDTO.ClistStatisticResponse(
                    componentId,
                    count
                )
            }
    }

}