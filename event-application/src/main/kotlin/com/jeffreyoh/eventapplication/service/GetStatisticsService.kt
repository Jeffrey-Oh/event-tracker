package com.jeffreyoh.eventapplication.service

import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.input.GetEventStatisticsUseCase
import com.jeffreyoh.eventport.output.StatisticsRedisPort
import reactor.core.publisher.Mono

class GetStatisticsService(
    private val statisticsRedisPort: StatisticsRedisPort
): GetEventStatisticsUseCase {

    override fun getCount(componentId: Long, eventType: EventType): Mono<Long> {
        return statisticsRedisPort.getCount(componentId, eventType)
    }

}