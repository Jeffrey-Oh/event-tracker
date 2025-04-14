package com.jeffreyoh.eventtracker.application.model.event

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.core.domain.event.Event
import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata

class EventCommand {

    data class SaveEvent(
        val eventType: EventType,
        val userId: Long?,
        val sessionId: String,
        val metadata: EventMetadata,
    ) {
        fun toEvent(): Event {
            return Event(
                eventType = eventType,
                userId = userId,
                sessionId = sessionId,
                metadata = metadata,
            )
        }
    }

}