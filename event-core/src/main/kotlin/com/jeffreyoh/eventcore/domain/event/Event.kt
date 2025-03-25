package com.jeffreyoh.eventcore.domain.event

import java.time.LocalDateTime

data class Event(
    val eventId: Long = 0L,
    val eventType: EventType,
    val userId: Long?,
    val metadata: EventMetadata,
    val createdAt: LocalDateTime = LocalDateTime.now(),
)