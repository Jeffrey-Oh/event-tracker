package com.jeffreyoh.userservice.storage.entity

import com.jeffreyoh.userservice.core.domain.post.PostLike
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("post_like")
data class PostLikeEntity(

    @Id val postLikeId: Long? = null,
    val userId: Long,
    val postId: Long,
    val likedAt: LocalDateTime

) {

    companion object {
        fun fromDomain(postLike: PostLike): PostLikeEntity {
            return PostLikeEntity(
                postLikeId = if (postLike.postLikeId == 0L) null else postLike.postLikeId,
                userId = postLike.userId,
                postId = postLike.postId,
                likedAt = postLike.likedAt
            )
        }
    }

    fun toDomain(): PostLike {
        return PostLike(
            postLikeId = postLikeId ?: 0L,
            userId = userId,
            postId = postId,
            likedAt = likedAt
        )
    }

}
