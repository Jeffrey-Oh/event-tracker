package com.jeffreyoh.eventapi.adapter.inbound.web.dto

import com.jeffreyoh.eventcore.domain.event.EventCommand
import com.jeffreyoh.eventcore.domain.event.EventMetadata
import com.jeffreyoh.eventcore.domain.event.EventType

data class SaveEventRequest(
    val eventType: EventType,
    val userId: Long?,
    val metadata: EventMetadata,
) {
    fun toCommand(): EventCommand.SaveEventCommand {
        return EventCommand.SaveEventCommand(
            eventType,
            userId,
            metadata
        )
    }
}