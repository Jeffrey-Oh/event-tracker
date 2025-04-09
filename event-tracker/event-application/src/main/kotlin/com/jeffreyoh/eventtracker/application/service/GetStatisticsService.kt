package com.jeffreyoh.eventtracker.application.service

import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.port.input.GetEventStatisticsUseCase
import com.jeffreyoh.eventtracker.port.output.StatisticsRedisPort
import reactor.core.publisher.Mono

class GetStatisticsService(
    private val statisticsRedisPort: StatisticsRedisPort
): GetEventStatisticsUseCase {

    override fun getCount(componentId: Long, eventType: EventType): Mono<Long> {
        return when(eventType) {
            EventType.CLICK -> statisticsRedisPort.getEventCount(eventType, EventMetadata(componentId = componentId))
            EventType.PAGE_VIEW -> statisticsRedisPort.getEventCount(eventType, EventMetadata(componentId = componentId))
            EventType.SEARCH -> statisticsRedisPort.getEventCount(eventType, EventMetadata(componentId = componentId))
            EventType.LIKE -> Mono.error(IllegalArgumentException("Like count is not supported"))
            EventType.UNLIKE ->Mono.error(IllegalArgumentException("UnLike count is not supported"))
        }
    }

    override fun getLikeCount(componentId: Long, postId: Long): Mono<Long> {
        return statisticsRedisPort.getEventCount(EventType.LIKE, EventMetadata(componentId = componentId, postId = postId))
    }

}