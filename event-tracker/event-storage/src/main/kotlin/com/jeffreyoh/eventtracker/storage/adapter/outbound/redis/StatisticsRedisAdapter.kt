package com.jeffreyoh.eventtracker.storage.adapter.outbound.redis

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.model.event.EventRedisQuery
import com.jeffreyoh.eventtracker.application.model.statistics.GetStatisticsRedisQuery
import com.jeffreyoh.eventtracker.application.port.out.StatisticsRedisPort
import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
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

    override fun saveEventCount(query: EventRedisQuery): Mono<Void> {
        val key = buildKey(query.eventType, query.metadata)

        if (query.eventType == EventType.UNLIKE)
            return decrementLike(query.metadata.componentId, query.metadata.postId!!)

        return redisTemplate.opsForValue()
            .increment(key)
            .then()
    }

    override fun getEventCount(query: GetStatisticsRedisQuery): Mono<Long> {
        val key = buildKey(
            query.eventType,
            EventMetadata(
                componentId = query.componentId,
                postId = query.postId,
                keyword = query.keyword
            )
        )
        return redisTemplate.opsForValue().get(key).map { it.toLong() }.defaultIfEmpty(0L)
    }

    private fun buildKey(eventType: EventType, metadata: EventMetadata): String {
        val key = "statistics:${eventType.name.lowercase()}:component:${metadata.componentId}"
        return when (eventType) {
            EventType.SEARCH -> "$key:keyword:${metadata.keyword}"
            EventType.LIKE, EventType.UNLIKE ->
                "statistics:${EventType.LIKE.name.lowercase()}:component:${metadata.componentId}:post:${metadata.postId}"
            else -> key
        }
    }

    fun decrementLike(
        componentId: Long,
        postId: Long
    ): Mono<Void> {
        val key = buildKey(EventType.UNLIKE, EventMetadata(componentId = EventType.UNLIKE.componentId, postId = postId))
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