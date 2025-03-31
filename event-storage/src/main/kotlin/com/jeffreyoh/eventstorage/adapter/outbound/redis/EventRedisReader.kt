package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventport.output.ReadEventPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Component
class EventRedisReader(
    private val redisTemplate: ReactiveStringRedisTemplate
): ReadEventPort {

    override fun readLikeFromRedisKey(key: String): Mono<String> {
        log.info { "Reading event to Redis" }

        return redisTemplate.opsForValue()
            .get(key)
            .switchIfEmpty(Mono.empty())
    }

}