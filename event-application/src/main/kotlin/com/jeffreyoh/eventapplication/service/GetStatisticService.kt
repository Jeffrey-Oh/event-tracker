package com.jeffreyoh.eventapplication.service

import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.input.GetEventStatisticUseCase
import com.jeffreyoh.eventport.output.GetStatisticCountPort
import reactor.core.publisher.Mono

class GetStatisticService(
    private val getStatisticCountPort: GetStatisticCountPort
): GetEventStatisticUseCase {

    override fun getCount(componentId: Long, eventType: EventType): Mono<Long> {
        return getStatisticCountPort.getCount(componentId, eventType)
    }

}