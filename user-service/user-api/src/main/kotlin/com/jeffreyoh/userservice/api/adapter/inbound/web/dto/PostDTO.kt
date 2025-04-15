package com.jeffreyoh.userservice.api.adapter.inbound.web.dto

import com.jeffreyoh.userservice.application.model.post.PostCommand
import com.jeffreyoh.userservice.application.model.post.PostLikeCommand
import com.jeffreyoh.userservice.application.model.post.SearchKeywordSaveRedisByLikeResult
import com.jeffreyoh.userservice.core.domain.post.Post

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
        fun toCommand(): PostLikeCommand.TogglePostLike {
            return PostLikeCommand.TogglePostLike(
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
        val updatedAt: String,
        val likeCount: Long,
    ) {
        companion object {
            fun fromDomain(post: SearchKeywordSaveRedisByLikeResult): PostResponse {
                return PostResponse(
                    postId = post.postId,
                    userId = post.userId,
                    content = post.content,
                    imageUrls = post.imageUrls,
                    hashtags = post.hashtags,
                    createdAt = post.createdAt.toString(),
                    updatedAt = post.updatedAt.toString(),
                    likeCount = post.likeCount,
                )
            }
        }
    }

}