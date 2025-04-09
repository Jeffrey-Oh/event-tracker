package com.jeffreyoh.eventtracker.storage.adapter.outbound.redis

import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.port.output.StatisticsRedisPort
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatisticsRedisAdapter(
    private val redisTemplate: ReactiveRedisTemplate<String, Long>
): StatisticsRedisPort {

    private fun keyPostLike(componentId: Long, postId: Long): String
        = "statistics:${EventType.LIKE.name.lowercase()}:component:$componentId:post:$postId"

    private fun incrementCount(key: String): Mono<Void> {
        return redisTemplate.opsForValue()
            .increment(key)
            .then()
    }

    override fun incrementEventCount(eventType: EventType, metadata: EventMetadata): Mono<Void> {
        val key = buildKey(eventType, metadata)
        return redisTemplate.opsForValue().increment(key).then()
    }

    override fun getEventCount(eventType: EventType, metadata: EventMetadata): Mono<Long> {
        val key = buildKey(eventType, metadata)
        return redisTemplate.opsForValue().get(key).map { it.toLong() }.defaultIfEmpty(0L)
    }

    private fun buildKey(eventType: EventType, metadata: EventMetadata): String {
        return when (eventType) {
            EventType.LIKE -> "statistics:${eventType.name}:componentId:${eventType.componentId}:postId:${metadata.postId}"
            EventType.CLICK -> "statistics:${eventType.name}:componentId:${eventType.componentId}"
            EventType.PAGE_VIEW -> "statistics:${eventType.name}:componentId:${eventType.componentId}"
            EventType.SEARCH -> "statistics:${eventType.name}:componentId:${eventType.componentId}:keyword:${metadata.keyword}"
            else -> throw IllegalArgumentException("Unsupported event type")
        }
    }

    override fun incrementLike(componentId: Long, postId: Long): Mono<Void> {
        val key = buildKey(EventType.LIKE, EventMetadata(componentId = componentId, postId = postId))
        return incrementCount(key)
    }

    override fun decrementLike(
        componentId: Long,
        postId: Long
    ): Mono<Void> {
        val key = keyPostLike(componentId, postId)
        val luaScript = RedisScript.of(
            """
            local current = redis.call('GET', KEYS[1])
            if current and tonumber(current) > 0 then
                return redis.call('DECR', KEYS[1])
            else
                return current or 0
            end
            """.trimIndent(),
            Long::class.java
        )
        return redisTemplate.execute(luaScript, listOf(key))
            .then()
    }

}