package com.jeffreyoh.eventcore.domain.event

class EventCommand {

    data class SaveEventCommand(
        val eventType: EventType,
        val userId: Long?,
        val metadata: EventMetadata,
    )

}