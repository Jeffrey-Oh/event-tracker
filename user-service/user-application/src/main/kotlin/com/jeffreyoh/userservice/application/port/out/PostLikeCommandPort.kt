package com.jeffreyoh.userservice.application.port.out

import com.jeffreyoh.userservice.core.domain.post.PostLike
import reactor.core.publisher.Mono

interface PostLikeCommandPort {

    fun findByUserIdAndPostId(userId: Long, postId: Long): Mono<PostLike>
    fun save(postLike: PostLike): Mono<PostLike>
    fun delete(userId: Long, postId: Long): Mono<Void>

}