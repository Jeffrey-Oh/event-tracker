package com.jeffreyoh.eventtracker.port.output

import com.jeffreyoh.eventtracker.core.domain.event.EventCommand
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface StatisticsRedisPort {

    fun incrementEventCount(eventType: EventType, metadata: EventCommand.EventMetadata): Mono<Void>
    fun getEventCount(eventType: EventType, metadata: EventCommand.EventMetadata): Mono<Long>

    fun incrementLike(componentId: Long, postId: Long): Mono<Void>
    fun decrementLike(componentId: Long, postId: Long): Mono<Void>

    fun scan(): Flux<String>
    fun getCount(key: String): Mono<Long>
    fun saveCountSnapshot(key: String): Mono<Long>

}