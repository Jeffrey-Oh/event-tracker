package com.jeffreyoh.userservice.port.out

import com.jeffreyoh.userservice.core.domain.post.Post
import reactor.core.publisher.Flux

interface PostSearchPort {

    fun searchByKeyword(keyword: String, limit: Int = 10): Flux<Post>

}