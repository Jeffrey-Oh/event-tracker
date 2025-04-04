package com.jeffreyoh.userservice.storage.entity

import com.jeffreyoh.userservice.core.domain.Post
import com.jeffreyoh.userservice.core.domain.Visibility
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("post")
data class PostEntity(
    @Id val postId: Long? = null,
    val userId: Long,
    val content: String,
    val imageUrls: String?,
    val hashTags: List<String>? = emptyList(),
    val visibility: Visibility = Visibility.PUBLIC,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
) {

    companion object {
        fun fromDomain(post: Post): PostEntity {
            return PostEntity(
                postId = if (post.postId == 0L) null else post.postId,
                userId = post.userId,
                content = post.content,
                imageUrls = post.imageUrls,
                hashTags = post.hashTags,
                visibility = post.visibility,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt
            )
        }
    }

    fun toDomain(): Post {
        return Post(
            postId = postId!!,
            userId = userId,
            content = content,
            imageUrls = imageUrls,
            hashTags = hashTags,
            visibility = visibility,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

}