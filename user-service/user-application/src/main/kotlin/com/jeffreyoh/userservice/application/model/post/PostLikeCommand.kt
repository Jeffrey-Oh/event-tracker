package com.jeffreyoh.userservice.application.model.post

import com.jeffreyoh.userservice.core.domain.post.PostLike

class PostLikeCommand {

    data class TogglePostLike(
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