package com.jeffreyoh.userservice.core.domain

class EventTrackerCommand {

    data class PayloadCommand(
        val eventType: EventType,
        val userId: Long,
        val postId: Long
    )

    enum class EventType {
        SEARCH,
        LIKE
    }

    data class SearchCommand(
        val eventType: EventType,
        val userId: Long,
        val keyword: String
    )

}