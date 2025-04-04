package com.jeffreyoh.userservice.port.out

import com.jeffreyoh.userservice.core.domain.Post
import reactor.core.publisher.Mono

interface PostCommandPort {

    fun save(post: Post): Mono<Post>

}