package com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository

import com.jeffreyoh.userservice.storage.entity.PostLikeEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface PostLikeRepository : ReactiveCrudRepository<PostLikeEntity, Long> {

    fun findByUserIdAndPostId(userId: Long, postId: Long): Mono<PostLikeEntity>
    fun deleteByPostLikeId(postLikeId: Long): Mono<Void>

}