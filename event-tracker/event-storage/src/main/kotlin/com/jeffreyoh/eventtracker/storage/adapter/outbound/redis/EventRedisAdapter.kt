package com.jeffreyoh.eventtracker.storage.adapter.outbound.redis

import com.jeffreyoh.eventtracker.application.port.out.EventRedisPort
import com.jeffreyoh.eventtracker.core.domain.event.Event
import com.jeffreyoh.eventtracker.core.domain.event.toJson
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

private val log = KotlinLogging.logger {}

@Component
class EventRedisAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): EventRedisPort {

    override fun saveToRedis(event: Event): Mono<Void> {
        log.info { "Saving event to Redis: $event" }

        val userOrSessionIdKey = event.userId?.let { "user:$it" } ?: "session:${event.sessionId}"
        val key = "events:${event.eventType.name.lowercase()}:$userOrSessionIdKey"
        val value = event.toJson()

        return redisTemplate.opsForValue()
            .set(key, value, Duration.ofMinutes(10))
            .then()
    }

}