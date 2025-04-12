package com.jeffreyoh.userservice.core.command

import com.jeffreyoh.userservice.core.domain.Post

class PostCommand {

    data class CreatePostCommand(
        val userId: Long,
        val content: String,
        val imageUrls: String?,
        val hashtags: List<String>?
    ) {
        fun toPost(): Post {
            return Post(
                userId = userId,
                content = content,
                imageUrls = imageUrls,
                hashtags = hashtags
            )
        }
    }

}