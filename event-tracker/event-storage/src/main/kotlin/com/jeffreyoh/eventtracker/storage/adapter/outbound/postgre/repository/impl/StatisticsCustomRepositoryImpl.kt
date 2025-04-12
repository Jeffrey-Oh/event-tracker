package com.jeffreyoh.eventtracker.storage.adapter.outbound.postgre.repository.impl

import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.storage.adapter.outbound.postgre.repository.StatisticsCustomRepository
import com.jeffreyoh.eventtracker.storage.entity.StatisticsEntity
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
class StatisticsCustomRepositoryImpl(
    private val databaseClient: DatabaseClient
) : StatisticsCustomRepository {

    override fun save(statisticsEntity: StatisticsEntity): Mono<Void> {
        val query = """
            INSERT INTO statistics (event_type, metadata, count, updated_at)
            VALUES (:eventType, :metadata::jsonb, :count, :updatedAt)
            ON CONFLICT (event_type, metadata) DO UPDATE
            SET count = statistics.count + EXCLUDED.count,
                updated_at = EXCLUDED.updated_at
        """.trimIndent()

        return databaseClient.sql(query)
            .bind("eventType", statisticsEntity.eventType.eventName)
            .bind("metadata", statisticsEntity.metadata)
            .bind("count", statisticsEntity.count)
            .bind("updatedAt", statisticsEntity.updatedAt)
            .then()
    }

    override fun findByEventType(eventType: EventType): Mono<StatisticsEntity> {
        val query = """
            SELECT
                event_type,
                metadata::text AS metadata,
                count,
                updated_at
            FROM statistics
            WHERE event_type = :eventType
        """.trimIndent()

        return databaseClient.sql(query)
            .bind("eventType", eventType)
            .map { row, _ ->
                StatisticsEntity(
                    statisticsId = row.get("statistics_id", Long::class.java)!!,
                    eventType = row.get("event_type", EventType::class.java)!!,
                    metadata = row.get("metadata", String::class.java)!!,
                    count = row.get("count", Long::class.java)!!,
                    updatedAt = row.get("updated_at", LocalDateTime::class.java)!!
                )
            }
            .one()
    }

}