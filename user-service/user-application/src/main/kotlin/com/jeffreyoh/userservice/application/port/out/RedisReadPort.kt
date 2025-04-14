package com.jeffreyoh.userservice.application.port.out

import reactor.core.publisher.Mono

interface RedisReadPort {

    fun recentSearchByKeyword(userId: Long): Mono<List<String>>

}