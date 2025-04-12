package com.jeffreyoh.eventtracker.storage.adapter.outbound.redis

import com.jeffreyoh.eventtracker.core.domain.event.EventCommand
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.port.output.StatisticsRedisPort
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class StatisticsRedisAdapter(
    private val redisTemplate: ReactiveRedisTemplate<String, Long>
): StatisticsRedisPort {

    private fun incrementCount(key: String): Mono<Void> {
        return redisTemplate.opsForValue()
            .increment(key)
            .then()
    }

    override fun incrementEventCount(eventType: EventType, metadata: EventCommand.EventMetadata): Mono<Void> {
        val key = buildKey(eventType, metadata)
        return redisTemplate.opsForValue().increment(key).then()
    }

    override fun getEventCount(eventType: EventType, metadata: EventCommand.EventMetadata): Mono<Long> {
        val key = buildKey(eventType, metadata)
        return redisTemplate.opsForValue().get(key).map { it.toLong() }.defaultIfEmpty(0L)
    }

    private fun buildKey(eventType: EventType, metadata: EventCommand.EventMetadata): String {
        return when (eventType) {
            EventType.CLICK -> "statistics:${eventType.name}:componentId:${eventType.componentId}"
            EventType.PAGE_VIEW -> "statistics:${eventType.name}:componentId:${eventType.componentId}"
            EventType.SEARCH -> "statistics:${eventType.name}:componentId:${eventType.componentId}:keyword:${metadata.keyword}"
            else -> "statistics:${EventType.LIKE.name.lowercase()}:componentId:${eventType.componentId}:postId:${metadata.postId}"
        }
    }

    override fun incrementLike(componentId: Long, postId: Long): Mono<Void> {
        val key = buildKey(EventType.LIKE, EventCommand.EventMetadata(componentId = componentId, postId = postId))
        return incrementCount(key)
    }

    override fun decrementLike(
        componentId: Long,
        postId: Long
    ): Mono<Void> {
        val key = buildKey(EventType.UNLIKE, EventCommand.EventMetadata(componentId = componentId, postId = postId))
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

    override fun scan(): Flux<String> {
        return redisTemplate.scan(
            ScanOptions.scanOptions()
                .match("statistics:*")
                .count(1000)
                .build()
        )
    }

    override fun getCount(key: String): Mono<Long> {
        return redisTemplate.opsForValue()
            .get(key)
            .map { it.toLong() }
            .onErrorReturn(NumberFormatException::class.java, 0L)
            .defaultIfEmpty(0L)
    }

    override fun saveCountSnapshot(key: String): Mono<Long> {
        val script = RedisScript.of("""
                local current = tonumber(redis.call('GET', KEYS[1]) or '0')
                local snapshot = tonumber(redis.call('GET', KEYS[2]) or '0')
                local delta = current - snapshot
                if delta <= 0 then return 0 end
                redis.call('SET', KEYS[2], current)
                return delta
            """.trimIndent(),
            Long::class.java
        )

        val snapshotKey = key.replaceFirst("statistics:", "statistics_snapshot:")

        return redisTemplate.execute(script, listOf(key, snapshotKey))
            .next()
            .map { it ?: 0L }
    }

}