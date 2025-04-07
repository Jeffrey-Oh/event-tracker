package com.jeffreyoh.userservice.port.`in`

import com.jeffreyoh.userservice.core.domain.Post
import reactor.core.publisher.Flux

interface SearchUseCase {

    fun searchByKeyword(userId: Long, keyword: String): Flux<Post>

}