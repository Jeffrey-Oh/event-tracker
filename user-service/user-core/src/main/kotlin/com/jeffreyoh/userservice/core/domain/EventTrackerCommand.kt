package com.jeffreyoh.userservice.core.domain

class EventTrackerCommand {

    data class PayloadCommand(
        val eventType: EventType,
        val userId: Long,
        val postId: Long
    )

    enum class EventType {
        LIKE
    }

}