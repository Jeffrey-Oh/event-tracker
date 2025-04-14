package com.jeffreyoh.eventtracker.application.port.`in`

import com.jeffreyoh.enums.EventType
import reactor.core.publisher.Mono

interface GetEventStatisticsUseCase {

    fun getCount(componentId: Long, eventType: EventType): Mono<Long>
    fun getLikeCount(componentId: Long, postId: Long): Mono<Long>

}