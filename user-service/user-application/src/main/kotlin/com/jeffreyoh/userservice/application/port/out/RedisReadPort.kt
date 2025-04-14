package com.jeffreyoh.userservice.application.port.out

import reactor.core.publisher.Mono

interface RedisReadPort {

    fun getLikeCheck(userId: Long, postId: Long): Mono<Boolean>
    fun recentSearchByKeyword(userId: Long): Mono<List<String>>

}