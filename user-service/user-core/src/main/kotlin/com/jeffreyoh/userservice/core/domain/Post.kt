package com.jeffreyoh.userservice.core.domain

import java.time.LocalDateTime

data class Post(
    val postId: Long = 0L,
    val userId: Long,
    val content: String,
    val imageUrls: String?,
    val hashtags: List<String>? = emptyList(),
    val visibility: Visibility = Visibility.PUBLIC,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
)