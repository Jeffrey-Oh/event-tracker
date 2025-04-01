package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.output.StatisticsRedisPort
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatisticsRedisAdapter(
    private val redisTemplate: ReactiveRedisTemplate<String, Long>
): StatisticsRedisPort {

    private fun keyEventDefault(eventType: EventType, componentId: Long): String
        = "statistics:${eventType.name.lowercase()}:component:$componentId"

    private fun keyPostLike(componentId: Long, postId: Long): String
        = "statistics:${EventType.LIKE.name.lowercase()}:component:$componentId:post:$postId"

    private fun getCount(key: String): Mono<Long> {
        return redisTemplate.opsForValue()
            .get(key)
            .mapNotNull { it.toLong() }
            .defaultIfEmpty(0L)
    }

    override fun getLikeCount(componentId: Long, postId: Long): Mono<Long> {
        return getCount(keyPostLike(componentId, postId))
    }

    override fun getClickCount(componentId: Long): Mono<Long> {
        return getCount(keyEventDefault(EventType.CLICK, componentId))
    }

    override fun getPageViewCount(componentId: Long): Mono<Long> {
        return getCount(keyEventDefault(EventType.PAGE_VIEW, componentId))
    }

    override fun getSearchCount(componentId: Long): Mono<Long> {
        return getCount(keyEventDefault(EventType.SEARCH, componentId))
    }

    private fun incrementCount(key: String): Mono<Void> {
        return redisTemplate.opsForValue()
            .increment(key)
            .then()
    }

    override fun incrementClick(componentId: Long): Mono<Void> {
        return incrementCount(keyEventDefault(EventType.CLICK, componentId))
    }

    override fun incrementPageView(componentId: Long): Mono<Void> {
        return incrementCount(keyEventDefault(EventType.PAGE_VIEW, componentId))
    }

    override fun incrementSearch(componentId: Long): Mono<Void> {
        return incrementCount(keyEventDefault(EventType.SEARCH, componentId))
    }

    override fun incrementLike(componentId: Long, postId: Long): Mono<Void> {
        return incrementCount(keyPostLike(componentId, postId))
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