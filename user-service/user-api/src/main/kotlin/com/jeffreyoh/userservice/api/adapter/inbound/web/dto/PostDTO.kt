package com.jeffreyoh.userservice.api.adapter.inbound.web.dto

import com.jeffreyoh.userservice.core.domain.Post
import com.jeffreyoh.userservice.core.command.PostCommand
import com.jeffreyoh.userservice.core.command.PostLikeCommand

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
                hashtags = hashtags
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

    data class PostResponse(
        val postId: Long,
        val userId: Long,
        val content: String,
        val imageUrls: String?,
        val hashtags: List<String>?,
        val createdAt: String,
        val updatedAt: String
    ) {
        companion object {
            fun fromDomain(post: Post): PostResponse {
                return PostResponse(
                    postId = post.postId,
                    userId = post.userId,
                    content = post.content,
                    imageUrls = post.imageUrls,
                    hashtags = post.hashtags,
                    createdAt = post.createdAt.toString(),
                    updatedAt = post.updatedAt.toString()
                )
            }
        }
    }

}