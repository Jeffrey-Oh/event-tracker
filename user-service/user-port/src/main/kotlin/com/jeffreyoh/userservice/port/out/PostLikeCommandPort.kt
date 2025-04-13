package com.jeffreyoh.userservice.port.out

import com.jeffreyoh.userservice.core.domain.PostLike
import reactor.core.publisher.Mono

interface PostLikeCommandPort {

    fun findByUserIdAndPostId(userId: Long, postId: Long): Mono<PostLike>
    fun save(postLike: PostLike): Mono<PostLike>
    fun delete(userId: Long, postId: Long): Mono<Void>

}