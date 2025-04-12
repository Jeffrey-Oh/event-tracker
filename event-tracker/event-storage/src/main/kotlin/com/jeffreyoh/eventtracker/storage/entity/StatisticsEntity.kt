package com.jeffreyoh.eventtracker.storage.entity

import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.core.domain.statistics.Statistics
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("statistics")
data class StatisticsEntity(
    @Id val statisticsId: Long? = null,
    val eventType: EventType,
    val metadata: String,
    val count: Long = 0L,
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {

    companion object {
        fun fromDomain(statistics: Statistics): StatisticsEntity {
            return StatisticsEntity(
                statisticsId = if (statistics.statisticsId == 0L) null else statistics.statisticsId,
                eventType = statistics.eventType,
                metadata = statistics.metadata,
                count = statistics.count,
                updatedAt = statistics.updatedAt
            )
        }
    }

    fun toDomain(): Statistics {
        return Statistics(
            statisticsId = statisticsId!!,
            eventType = eventType,
            metadata = metadata,
            count = count,
            updatedAt = updatedAt
        )
    }

}
