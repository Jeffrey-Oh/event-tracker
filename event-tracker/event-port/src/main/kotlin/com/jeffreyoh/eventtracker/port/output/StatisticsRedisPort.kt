package com.jeffreyoh.eventtracker.port.output

import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import reactor.core.publisher.Mono

interface StatisticsRedisPort {

    fun incrementEventCount(eventType: EventType, metadata: EventMetadata): Mono<Void>
    fun getEventCount(eventType: EventType, metadata: EventMetadata): Mono<Long>

    fun incrementLike(componentId: Long, postId: Long): Mono<Void>
    fun decrementLike(componentId: Long, postId: Long): Mono<Void>

}