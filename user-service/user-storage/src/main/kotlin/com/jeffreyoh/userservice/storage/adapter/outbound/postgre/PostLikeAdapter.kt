package com.jeffreyoh.userservice.storage.adapter.outbound.postgre

import com.jeffreyoh.userservice.core.domain.post.PostLike
import com.jeffreyoh.userservice.port.out.PostLikeCommandPort
import com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository.PostLikeCustomRepository
import com.jeffreyoh.userservice.storage.entity.PostLikeEntity
import com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository.PostLikeRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PostLikeAdapter(
    private val postLikeRepository: PostLikeRepository,
    private val postLikeCustomRepository: PostLikeCustomRepository
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
            .switchIfEmpty(Mono.empty())
    }

    override fun save(postLike: PostLike): Mono<PostLike> {
        return postLikeCustomRepository.save(PostLikeEntity.fromDomain(postLike))
            .map { savedPostLike ->
                PostLike(
                    postLikeId = savedPostLike.postLikeId!!,
                    userId = savedPostLike.userId,
                    postId = savedPostLike.postId,
                    likedAt = savedPostLike.likedAt
                )
            }
    }

    override fun delete(userId: Long, postId: Long): Mono<Void> {
        return postLikeRepository.deleteByUserIdAndPostId(userId, postId)
    }


}