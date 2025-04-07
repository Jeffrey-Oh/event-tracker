package com.jeffreyoh.userservice.api.adapter.inbound.web.dto

import com.jeffreyoh.userservice.core.domain.Post
import com.jeffreyoh.userservice.core.domain.PostCommand
import com.jeffreyoh.userservice.core.domain.PostLikeCommand

class PostDTO {

    data class CreatePostRequest(
        val userId: Long,
        val content: String,
        val imageUrls: String?,
        val hashTags: List<String>? = emptyList()
    ) {
        fun toCommand(): PostCommand.CreatePostCommand {
            return PostCommand.CreatePostCommand(
                userId = userId,
                content = content,
                imageUrls = imageUrls,
                hashTags = hashTags
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
        val hashTags: List<String>?,
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
                    hashTags = post.hashTags,
                    createdAt = post.createdAt.toString(),
                    updatedAt = post.updatedAt.toString()
                )
            }
        }
    }

}