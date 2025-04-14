package com.jeffreyoh.userservice.application.port.out

import reactor.core.publisher.Mono

interface RedisCommandPort {

    fun saveLikeCheck(userId: Long, postId: Long): Mono<Void>
    fun deleteLikeCheck(userId: Long, postId: Long): Mono<Void>
    fun saveRecentKeyword(userId: Long, keyword: String): Mono<Void>

}