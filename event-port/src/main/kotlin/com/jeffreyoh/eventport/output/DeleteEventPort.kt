package com.jeffreyoh.eventport.output

import reactor.core.publisher.Mono

interface DeleteEventPort {

    fun deleteFromRedisKey(key: String): Mono<Void>

}