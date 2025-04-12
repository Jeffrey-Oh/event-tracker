package com.jeffreyoh.eventtracker.port.output

import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.core.domain.statistics.Statistics
import reactor.core.publisher.Mono

interface StatisticsPostgrePort {

    fun save(statistics: Statistics): Mono<Void>
    fun findByEventType(eventType: EventType): Mono<Statistics>

}