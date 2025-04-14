package com.jeffreyoh.userservice.core.domain.post

import java.time.LocalDateTime

data class PostLike(
    val postLikeId: Long = 0L,
    val userId: Long,
    val postId: Long,
    val likedAt: LocalDateTime = LocalDateTime.now()
)
