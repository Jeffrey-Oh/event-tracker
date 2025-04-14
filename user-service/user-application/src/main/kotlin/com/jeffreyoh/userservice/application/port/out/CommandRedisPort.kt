package com.jeffreyoh.userservice.port.out

import reactor.core.publisher.Mono

interface CommandRedisPort {

    fun saveLikeCheck(userId: Long, postId: Long): Mono<Void>
    fun deleteLikeCheck(userId: Long, postId: Long): Mono<Void>

}