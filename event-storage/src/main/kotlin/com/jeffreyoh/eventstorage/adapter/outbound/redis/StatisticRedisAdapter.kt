package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.output.GetStatisticCountPort
import com.jeffreyoh.eventport.output.IncrementCountPort
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StatisticRedisAdapter(
    private val redisTemplate: ReactiveRedisTemplate<String, Long>
): IncrementCountPort, GetStatisticCountPort {

    companion object {
        private const val KEY_PREFIX = "statistics:%s:component:%d"
    }

    override fun incrementCount(componentId: Long, eventType: EventType): Mono<Void> {
        return redisTemplate.opsForValue()
            .increment(KEY_PREFIX.format(eventType.name.lowercase(), componentId))
            .then()
    }

    override fun getCount(componentId: Long, eventType: EventType): Mono<Long> {
        return redisTemplate.opsForValue()
            .get(KEY_PREFIX.format(eventType.name.lowercase(), componentId))
            .mapNotNull { it.toLong() }
            .defaultIfEmpty(0L)
    }

}