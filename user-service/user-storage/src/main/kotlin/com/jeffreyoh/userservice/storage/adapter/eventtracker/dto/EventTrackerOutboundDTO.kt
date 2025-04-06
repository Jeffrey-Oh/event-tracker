package com.jeffreyoh.userservice.storage.adapter.eventtracker.dto

import com.jeffreyoh.userservice.core.domain.EventTrackerCommand

class EventTrackerOutboundDTO {

    data class SaveEventRequest(
        val eventType: EventTrackerCommand.EventType,
        val userId: Long,
        val sessionId: String,
        val metadata: EventMetadata,
    )

    data class EventMetadata(
        val componentId: Long,
        val elementId: String,
        val keyword: String? = null,
        val postId: Long? = null,
    )

}