package com.jeffreyoh.eventtracker.storage.adapter.outbound.postgre

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.port.out.StatisticsPostgrePort
import com.jeffreyoh.eventtracker.core.domain.statistics.Statistics
import com.jeffreyoh.eventtracker.storage.adapter.outbound.postgre.repository.StatisticsCustomRepository
import com.jeffreyoh.eventtracker.storage.entity.StatisticsEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatisticsPostgreAdapter(
    private val statisticsCustomRepository: StatisticsCustomRepository
) : StatisticsPostgrePort {

    override fun save(statistics: Statistics): Mono<Void> {
        return statisticsCustomRepository.save(StatisticsEntity.fromDomain(statistics))
    }

    override fun findByEventType(eventType: EventType): Mono<Statistics> {
        return statisticsCustomRepository.findByEventType(eventType)
            .map {
                Statistics(
                    statisticsId = it.statisticsId!!,
                    eventType = it.eventType,
                    metadata = it.metadata,
                    count = it.count,
                    updatedAt = it.updatedAt
                )
            }
            .switchIfEmpty(Mono.error(IllegalStateException("Statistics not found for event type: $eventType")))
    }

}