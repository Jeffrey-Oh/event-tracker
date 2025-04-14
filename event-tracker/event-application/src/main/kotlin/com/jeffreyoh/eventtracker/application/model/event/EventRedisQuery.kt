package com.jeffreyoh.eventtracker.application.model.event

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata

data class EventRedisQuery(
    val eventType: EventType,
    val userId: Long?,
    val sessionId: String,
    val metadata: EventMetadata,
) {
    companion object {
        fun fromQuery(command: EventCommand.SaveEvent): EventRedisQuery {
            return EventRedisQuery(
                command.eventType,
                command.userId,
                command.sessionId,
                command.metadata,
            )
        }
    }
}
