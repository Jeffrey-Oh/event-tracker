package com.jeffreyoh.userservice.storage.adapter

import com.jeffreyoh.userservice.core.domain.PostLike
import com.jeffreyoh.userservice.port.out.PostLikeCommandPort
import com.jeffreyoh.userservice.storage.entity.PostLikeEntity
import com.jeffreyoh.userservice.storage.repository.PostLikeRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PostLikeAdapter(
    private val postLikeRepository: PostLikeRepository,
) : PostLikeCommandPort {

    override fun findByUserIdAndPostId(
        userId: Long,
        postId: Long
    ): Mono<PostLike> {
        return postLikeRepository.findByUserIdAndPostId(userId, postId)
            .map { postLike ->
                PostLike(
                    postLikeId = postLike.postLikeId!!,
                    userId = postLike.userId,
                    postId = postLike.postId,
                    likedAt = postLike.likedAt
                )
            }
            .switchIfEmpty(Mono.error(NoSuchElementException("PostLike not found")))
    }

    override fun save(postLike: PostLike): Mono<PostLike> {
        return postLikeRepository.save(PostLikeEntity.fromDomain(postLike))
            .map { savedPostLike ->
                PostLike(
                    postLikeId = savedPostLike.postLikeId!!,
                    userId = savedPostLike.userId,
                    postId = savedPostLike.postId,
                    likedAt = savedPostLike.likedAt
                )
            }
    }

    override fun delete(postLikeId: Long): Mono<Void> {
        return postLikeRepository.deleteByPostLikeId(postLikeId)
    }


}