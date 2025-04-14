package com.jeffreyoh.userservice.application.port.`in`

import com.jeffreyoh.userservice.core.domain.post.Post
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface SearchUseCase {

    fun searchByKeyword(userId: Long, keyword: String): Flux<Post>
    fun recentSearchByKeyword(userId: Long): Mono<List<String>>

}