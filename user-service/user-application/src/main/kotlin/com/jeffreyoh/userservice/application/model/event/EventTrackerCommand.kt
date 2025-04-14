package com.jeffreyoh.userservice.application.model.event

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.userservice.core.domain.event.EventMetadata

class EventTrackerCommand {

    data class SaveEvent(
        val eventType: EventType,
        val userId: Long?,
        val sessionId: String,
        val metadata: EventMetadata
    ) {
        fun toRequest(): EventTrackerRequest.SaveEvent {
            return EventTrackerRequest.SaveEvent(
                eventType,
                userId,
                sessionId,
                metadata
            )
        }
    }

}