package com.jeffreyoh.eventtracker.core.domain.statistics

import com.jeffreyoh.eventtracker.core.domain.event.EventType
import java.time.LocalDateTime

data class Statistics(
    val statisticsId: Long = 0L,
    val eventType: EventType,
    val metadata: String,
    val count: Long = 0L,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {

    companion object {
        fun toDomain(eventType: EventType, metadata: String, count: Long): Statistics {
            return Statistics(
                eventType = eventType,
                metadata = metadata,
                count = count,
                updatedAt = LocalDateTime.now()
            )
        }
    }

}
