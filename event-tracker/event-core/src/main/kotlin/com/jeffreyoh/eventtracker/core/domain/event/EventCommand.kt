package com.jeffreyoh.eventtracker.core.domain.event

class EventCommand {

    data class SaveEventCommand(
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