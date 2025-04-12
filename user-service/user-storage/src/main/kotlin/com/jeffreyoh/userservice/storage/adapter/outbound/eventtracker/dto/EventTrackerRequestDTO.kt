package com.jeffreyoh.userservice.storage.adapter.outbound.eventtracker.dto

import com.jeffreyoh.userservice.core.command.EventTrackerCommand
import com.jeffreyoh.userservice.core.domain.EventType

class EventTrackerRequestDTO {

    data class SaveEventRequest(
        val eventType: EventType,
        val userId: Long?,
        val sessionId: String,
        val metadata: EventTrackerCommand.EventMetadata
    )

}