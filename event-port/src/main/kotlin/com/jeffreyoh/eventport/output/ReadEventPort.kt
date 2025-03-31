package com.jeffreyoh.eventport.output

import reactor.core.publisher.Mono

interface ReadEventPort {

    fun readLikeFromRedisKey(key: String): Mono<String>

}