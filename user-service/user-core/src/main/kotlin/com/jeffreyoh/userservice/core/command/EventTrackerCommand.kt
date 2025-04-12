package com.jeffreyoh.userservice.core.command

import com.jeffreyoh.userservice.core.domain.EventType

class EventTrackerCommand {

    data class SaveEventCommand(
        val eventType: EventType,
        val userId: Long?,
        val sessionId: String,
        val metadata: EventMetadata
    )

    data class EventMetadata(
        val componentId: Long,
        val elementId: String?,
        val keyword: String?,
        val postId: Long?
    )

}