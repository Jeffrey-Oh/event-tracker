package com.jeffreyoh.userservice.application.model.post

import com.jeffreyoh.userservice.core.domain.post.Visibility
import java.time.LocalDateTime

data class SearchKeywordSaveRedisByLikeResult(
    val postId: Long,
    val userId: Long,
    val content: String,
    val imageUrls: String?,
    val hashtags: List<String>? = emptyList(),
    val visibility: Visibility = Visibility.PUBLIC,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
    val likeCount: Long = 0L,
)
