package com.jeffreyoh.eventtracker.application.port.out

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.model.event.EventRedisQuery
import com.jeffreyoh.eventtracker.application.model.statistics.GetStatisticsRedisQuery
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface StatisticsRedisPort {

    fun saveEventCount(query: EventRedisQuery): Mono<Void>
    fun getEventCount(query: GetStatisticsRedisQuery): Mono<Long>

    fun saveCountSnapshot(key: String): Mono<Long>

    fun getEventCountsForHour(eventType: EventType, time: String): Flux<Pair<String, Long>>

}