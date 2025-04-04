package com.jeffreyoh.userservice.storage.adapter.dto

import com.jeffreyoh.userservice.core.domain.EventTrackerCommand

class EventTrackerOutboundDTO {

    data class SaveEventRequest(
        val eventType: EventTrackerCommand.EventType,
        val userId: Long,
        val metadata: EventMetadata,
    )

    data class EventMetadata(
        val componentId: Long,
        val elementId: String,
        val keyword: String? = null,
        val postId: Long? = null,
    )

}