package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.Event
import com.jeffreyoh.eventcore.domain.event.toJson
import com.jeffreyoh.eventport.output.SaveEventPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

private val log = KotlinLogging.logger {}

@Component
class SaveEventRedisAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): SaveEventPort {

    override fun saveToRedis(event: Event): Mono<Void> {
        log.info { "Saving event to Redis: $event" }

        val idKey = event.userId?.let { "user:$it" } ?: "session:${event.sessionId}"
        val key = "events:${event.eventType.name}:${idKey}"
        val value = event.toJson()

        return redisTemplate.opsForValue()
            .set(key, value, Duration.ofMinutes(10))
            .then()
            .log()
    }

}