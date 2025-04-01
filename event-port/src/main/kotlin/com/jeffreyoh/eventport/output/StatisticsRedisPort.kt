package com.jeffreyoh.eventport.output

import com.jeffreyoh.eventcore.domain.event.EventType
import reactor.core.publisher.Mono

interface StatisticsRedisPort {

    fun getCount(componentId: Long, eventType: EventType): Mono<Long>

    fun incrementCount(componentId: Long, eventType: EventType): Mono<Void>
    fun incrementLikeCount(componentId: Long, postId: Long): Mono<Void>

    fun decrementLikeCount(componentId: Long, postId: Long): Mono<Void>

}