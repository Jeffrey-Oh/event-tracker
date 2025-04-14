package com.jeffreyoh.eventtracker.application.port.out

import com.jeffreyoh.eventtracker.application.model.event.EventRedisQuery
import com.jeffreyoh.eventtracker.application.model.statistics.GetStatisticsRedisQuery
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface StatisticsRedisPort {

    fun saveEventCount(query: EventRedisQuery): Mono<Void>
    fun getEventCount(query: GetStatisticsRedisQuery): Mono<Long>

    fun scan(): Flux<String>
    fun saveCountSnapshot(key: String): Mono<Long>

}