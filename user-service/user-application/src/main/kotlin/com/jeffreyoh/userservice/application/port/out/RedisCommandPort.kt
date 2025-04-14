package com.jeffreyoh.userservice.application.port.out

import reactor.core.publisher.Mono

interface RedisCommandPort {

    fun saveRecentKeyword(userId: Long, keyword: String): Mono<Void>

}