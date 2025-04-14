package com.jeffreyoh.eventtracker.application.port.out

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.model.event.EventCommand
import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface StatisticsRedisPort {

    fun incrementEventCount(eventType: EventType, metadata: EventMetadata): Mono<Void>
    fun getEventCount(eventType: EventType, metadata: EventMetadata): Mono<Long>

    fun incrementLike(componentId: Long, postId: Long): Mono<Void>
    fun decrementLike(componentId: Long, postId: Long): Mono<Void>

    fun scan(): Flux<String>
    fun getCount(key: String): Mono<Long>
    fun saveCountSnapshot(key: String): Mono<Long>

}