package com.jeffreyoh.userservice.application.model.event

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.userservice.core.domain.event.EventMetadata

class EventTrackerRequest {

    data class SaveEvent(
        val eventType: EventType,
        val userId: Long?,
        val sessionId: String,
        val metadata: EventMetadata
    )

}