package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.Event
import com.jeffreyoh.eventcore.domain.event.toJson
import com.jeffreyoh.eventport.output.DeleteEventPort
import com.jeffreyoh.eventport.output.SaveEventPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

private val log = KotlinLogging.logger {}

@Component
class EventRedisWriter(
    private val redisTemplate: ReactiveStringRedisTemplate
): SaveEventPort, DeleteEventPort {

    override fun saveToRedis(event: Event): Mono<Void> {
        log.info { "Saving event to Redis: $event" }

        val userOrSessionIdKey = event.userId?.let { "user:$it" } ?: "session:${event.sessionId}"
        val key = "events:${event.eventType.name.toString().lowercase()}:$userOrSessionIdKey"
        val value = event.toJson()

        return redisTemplate.opsForValue()
            .set(key, value, Duration.ofMinutes(10))
            .then()
    }

    override fun saveLikeEventToRedis(key: String, event: Event): Mono<Void> {
        log.info { "Saving like event to Redis: $key" }

        val value = event.toJson()

        return redisTemplate.opsForValue()
            .set(key, value)
            .then()
    }

    override fun deleteFromRedisKey(key: String): Mono<Void> {
        log.info { "Deleting event from Redis: $key" }

        return redisTemplate.delete(key)
            .doOnSuccess {
                log.info { "Successfully deleted event from Redis: $key" }
            }
            .doOnError {
                log.error { "Failed to delete event from Redis: $key" }
            }
            .then()
    }

}