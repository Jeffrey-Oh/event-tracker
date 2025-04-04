package com.jeffreyoh.eventtracker.port.input

import com.jeffreyoh.eventtracker.core.domain.event.EventType
import reactor.core.publisher.Mono

interface GetEventStatisticsUseCase {

    fun getCount(componentId: Long, eventType: EventType): Mono<Long>
    fun getLikeCount(componentId: Long, postId: Long): Mono<Long>

}