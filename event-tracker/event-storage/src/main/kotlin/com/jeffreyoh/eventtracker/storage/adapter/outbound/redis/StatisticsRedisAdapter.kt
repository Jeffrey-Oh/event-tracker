package com.jeffreyoh.eventtracker.storage.adapter.outbound.redis

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.model.event.EventRedisQuery
import com.jeffreyoh.eventtracker.application.model.statistics.GetStatisticsRedisQuery
import com.jeffreyoh.eventtracker.application.port.out.StatisticsRedisPort
import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class StatisticsRedisAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): StatisticsRedisPort {

    override fun saveEventCount(query: EventRedisQuery): Mono<Void> {
        val zsetKey = buildZSetKey(query.eventType)
        val member = buildZSetMember(query.metadata)

        if (query.eventType == EventType.UNLIKE)
            return decrementLike(zsetKey, member)

        return redisTemplate.opsForZSet()
            .incrementScore(zsetKey, member, 1.0)
            .then(
                redisTemplate.expire(zsetKey, Duration.ofMinutes(10))
                    .then()
            )
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

    private fun buildZSetKey(eventType: EventType): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
        return "statistics:${eventType.name.lowercase()}:$timestamp"
    }

    private fun buildZSetMember(metadata: EventMetadata): String {
        val member = "component:${metadata.componentId}"
        return when {
            metadata.postId != null -> "$member:post:${metadata.postId}"
            metadata.keyword != null -> "$member:keyword:${metadata.keyword}"
            else -> member
        }
    }

    private fun buildKey(eventType: EventType, metadata: EventMetadata): String {
        var key = "statistics:${eventType.name.lowercase()}:component:${metadata.componentId}"
        key = when (eventType) {
            EventType.SEARCH -> "$key:keyword:${metadata.keyword}"
            EventType.LIKE, EventType.UNLIKE ->
                "statistics:${EventType.LIKE.name.lowercase()}:component:${metadata.componentId}:post:${metadata.postId}"
            else -> key
        }
        return "$key:${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))}"
    }

    fun decrementLike(zsetKey: String, member: String): Mono<Void> {
        val luaScript = RedisScript.of(
            """
            local current = redis.call('ZSCORE', KEYS[1], ARGV[1])
            if current and tonumber(current) > 0 then
                return redis.call('ZINCRBY', KEYS[1], -1, ARGV[1])
            else
                return current or 0
            end
            """.trimIndent(), Long::class.java
        )
        return redisTemplate.execute(luaScript, listOf(zsetKey), listOf(member))
            .then()
    }

    override fun getEventCountsForHour(eventType: EventType, time: String): Flux<Pair<String, Long>> {
        val key = "statistics:${eventType.name.lowercase()}:$time"
        return redisTemplate.opsForZSet()
            .rangeWithScores(key, Range.unbounded())
            .map { it.value!! to it.score!!.toLong() }
    }

    override fun saveCountSnapshot(key: String): Mono<Long> {
        val script = RedisScript.of(
            """
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