package com.jeffreyoh.userservice.storage.entity

import com.jeffreyoh.userservice.core.domain.post.Post
import com.jeffreyoh.userservice.core.domain.post.Visibility
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("post")
data class PostEntity(
    @Id val postId: Long? = null,
    val userId: Long,
    val content: String,
    val imageUrls: String?,
    val hashtags: List<String>? = emptyList(),
    val visibility: Visibility = Visibility.PUBLIC,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime? = null,
) {

    companion object {
        fun fromDomain(post: Post): PostEntity {
            return PostEntity(
                postId = if (post.postId == 0L) null else post.postId,
                userId = post.userId,
                content = post.content,
                imageUrls = post.imageUrls,
                hashtags = post.hashtags,
                visibility = post.visibility,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt ?: LocalDateTime.now()
            )
        }
    }

    fun toDomain(): Post {
        return Post(
            postId = postId!!,
            userId = userId,
            content = content,
            imageUrls = imageUrls,
            hashtags = hashtags,
            visibility = visibility,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

}