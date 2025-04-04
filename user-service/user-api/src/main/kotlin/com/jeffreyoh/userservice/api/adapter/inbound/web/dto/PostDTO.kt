package com.jeffreyoh.userservice.api.adapter.inbound.web.dto

import com.jeffreyoh.userservice.core.domain.PostCommand
import com.jeffreyoh.userservice.core.domain.PostLikeCommand

class PostDTO {

    data class CreatePostRequest(
        val userId: Long,
        val content: String,
        val imageUrls: String?,
        val hashtags: List<String>? = emptyList()
    ) {
        fun toCommand(): PostCommand.CreatePostCommand {
            return PostCommand.CreatePostCommand(
                userId = userId,
                content = content,
                imageUrls = imageUrls,
                hashTags = hashtags
            )
        }
    }

    data class ToggleRequest(
        val postId: Long,
        val userId: Long,
    ) {
        fun toCommand(): PostLikeCommand.PostLikeCommand {
            return PostLikeCommand.PostLikeCommand(
                postId = postId,
                userId = userId,
            )
        }
    }

}