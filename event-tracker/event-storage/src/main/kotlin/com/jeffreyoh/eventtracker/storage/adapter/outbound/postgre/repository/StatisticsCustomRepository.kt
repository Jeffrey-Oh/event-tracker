package com.jeffreyoh.eventtracker.storage.adapter.outbound.postgre.repository

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.storage.entity.StatisticsEntity
import reactor.core.publisher.Mono

interface StatisticsCustomRepository {

    fun save(statisticsEntity: StatisticsEntity): Mono<Void>
    fun findByEventType(eventType: EventType): Mono<StatisticsEntity>

}