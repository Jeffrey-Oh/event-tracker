package com.jeffreyoh.userservice.port.`in`

import com.jeffreyoh.userservice.core.domain.Post
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface SearchUseCase {

    fun searchByKeyword(userId: Long, keyword: String): Flux<Post>
    fun recentSearchByKeyword(userId: Long): Mono<List<String>>

}