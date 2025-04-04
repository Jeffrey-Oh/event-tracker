package com.jeffreyoh.eventport.output

import com.jeffreyoh.eventcore.domain.event.Event
import reactor.core.publisher.Mono

interface EventRedisPort {

    fun readLikeFromRedisKey(key: String): Mono<String>

    fun saveToRedis(event: Event): Mono<Void>
    fun saveLikeEventToRedis(key: String, event: Event): Mono<Void>

    fun deleteFromRedisKey(key: String): Mono<Void>

}