package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.output.DecrementCountPort
import com.jeffreyoh.eventport.output.GetStatisticCountPort
import com.jeffreyoh.eventport.output.IncrementCountPort
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatisticRedisAdapter(
    private val redisTemplate: ReactiveRedisTemplate<String, Long>
): IncrementCountPort, DecrementCountPort, GetStatisticCountPort {

    companion object {
        private const val KEY_PREFIX_DEFAULT = "statistics:%s:component:%d"
        private const val KEY_PREFIX_POST_LIKE = "statistics:%s:component:%d:post:%d"
    }

    override fun incrementCount(componentId: Long, eventType: EventType): Mono<Void> {
        return redisTemplate.opsForValue()
            .increment(KEY_PREFIX_DEFAULT.format(eventType.name.lowercase(), componentId))
            .then()
    }

    override fun incrementLikeCount(
        componentId: Long,
        postId: Long
    ): Mono<Void> {
        return redisTemplate.opsForValue()
            .increment(KEY_PREFIX_POST_LIKE.format(EventType.LIKE.name.lowercase(), componentId, postId))
            .then()
    }

    override fun getCount(componentId: Long, eventType: EventType): Mono<Long> {
        return redisTemplate.opsForValue()
            .get(KEY_PREFIX_DEFAULT.format(eventType.name.lowercase(), componentId))
            .mapNotNull { it.toLong() }
            .defaultIfEmpty(0L)
    }

    override fun decrementLikeCount(
        componentId: Long,
        postId: Long
    ): Mono<Void> {
        val key = KEY_PREFIX_POST_LIKE.format(EventType.LIKE.name.lowercase(), componentId, postId)
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