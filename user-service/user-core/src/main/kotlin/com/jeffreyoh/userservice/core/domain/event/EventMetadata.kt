package com.jeffreyoh.userservice.core.domain.event

data class EventMetadata(
    val componentId: Long,
    val elementId: String?,
    val keyword: String?,
    val postId: Long?
)
