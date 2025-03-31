package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.output.DecrementCountPort
import com.jeffreyoh.eventport.output.GetStatisticCountPort
import com.jeffreyoh.eventport.output.IncrementCountPort
import org.springframework.data.redis.core.ReactiveRedisTemplate
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
        return redisTemplate.opsForValue()
            .decrement(KEY_PREFIX_POST_LIKE.format(EventType.LIKE.name.lowercase(), componentId, postId))
            .then()
    }

}