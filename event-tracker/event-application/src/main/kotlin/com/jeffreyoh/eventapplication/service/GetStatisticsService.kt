package com.jeffreyoh.eventapplication.service

import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.input.GetEventStatisticsUseCase
import com.jeffreyoh.eventport.output.StatisticsRedisPort
import reactor.core.publisher.Mono

class GetStatisticsService(
    private val statisticsRedisPort: StatisticsRedisPort
): GetEventStatisticsUseCase {

    override fun getCount(componentId: Long, eventType: EventType): Mono<Long> {
        return when(eventType) {
            EventType.CLICK -> statisticsRedisPort.getClickCount(componentId)
            EventType.PAGE_VIEW -> statisticsRedisPort.getPageViewCount(componentId)
            EventType.SEARCH -> statisticsRedisPort.getSearchCount(componentId)
            EventType.LIKE -> Mono.error(IllegalArgumentException("Like count is not supported"))
            EventType.UNLIKE ->Mono.error(IllegalArgumentException("UnLike count is not supported"))
        }
    }

    override fun getLikeCount(componentId: Long, postId: Long): Mono<Long> {
        return statisticsRedisPort.getLikeCount(componentId, postId)
    }

}