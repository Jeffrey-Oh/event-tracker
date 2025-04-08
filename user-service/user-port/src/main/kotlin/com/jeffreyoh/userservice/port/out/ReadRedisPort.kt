package com.jeffreyoh.userservice.port.out

import reactor.core.publisher.Mono

interface ReadRedisPort {

    fun recentSearchByKeyword(userId: Long): Mono<List<String>>

}