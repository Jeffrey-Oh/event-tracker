package com.jeffreyoh.userservice.port.out

import reactor.core.publisher.Mono

interface ReadRedisPort {

    fun getLikeCheck(userId: Long, postId: Long): Mono<Boolean>
    fun recentSearchByKeyword(userId: Long): Mono<List<String>>

}