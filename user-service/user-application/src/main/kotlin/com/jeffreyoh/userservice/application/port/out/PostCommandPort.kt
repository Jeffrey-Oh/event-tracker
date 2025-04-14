package com.jeffreyoh.userservice.application.port.out

import com.jeffreyoh.userservice.core.domain.post.Post
import reactor.core.publisher.Mono

interface PostCommandPort {

    fun save(post: Post): Mono<Post>

}