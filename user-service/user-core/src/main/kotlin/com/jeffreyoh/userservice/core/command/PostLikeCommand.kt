package com.jeffreyoh.userservice.core.command

import com.jeffreyoh.userservice.core.domain.PostLike

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