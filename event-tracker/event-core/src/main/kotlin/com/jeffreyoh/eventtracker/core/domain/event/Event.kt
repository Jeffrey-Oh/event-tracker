package com.jeffreyoh.eventtracker.core.domain.event

import java.time.LocalDateTime

data class Event(
    val eventId: Long = 0L,
    val eventType: EventType,
    val userId: Long?,
    val sessionId: String,
    val metadata: EventMetadata,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)

fun Event.toJson(): String {
    return """{
        "eventType": "${eventType.name}",
        "userId": $userId,
        "sessionId": $sessionId,
        "createdAt": "$createdAt",
        "metadata": {
            "componentId": "${metadata.componentId}",
            "elementId": "${metadata.elementId}",
            "keyword": "${metadata.keyword}",
            "postId": "${metadata.postId}"
        }
    }"""
}