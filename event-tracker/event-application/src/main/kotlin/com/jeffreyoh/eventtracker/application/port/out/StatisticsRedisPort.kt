package com.jeffreyoh.eventtracker.application.port.out

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.model.event.EventRedisQuery
import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface StatisticsRedisPort {

    fun saveEventCount(query: EventRedisQuery): Mono<Void>
    fun getEventCount(eventType: EventType, metadata: EventMetadata): Mono<Long>

    fun scan(): Flux<String>
    fun saveCountSnapshot(key: String): Mono<Long>

}