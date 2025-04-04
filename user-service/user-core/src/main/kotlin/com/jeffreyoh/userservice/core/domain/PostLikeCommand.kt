package com.jeffreyoh.userservice.core.domain

class PostLikeCommand {

    data class PostLikeCommand(
        val postId: Long,
        val userId: Long,
    ) {
        fun toPostLike(): PostLike {
            return PostLike(
                postId = postId,
                userId = userId,
            )
        }
    }

}