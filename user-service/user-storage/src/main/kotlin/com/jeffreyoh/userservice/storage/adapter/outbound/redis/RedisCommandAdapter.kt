package com.jeffreyoh.userservice.storage.adapter.outbound.redis

import com.jeffreyoh.userservice.application.port.out.RedisCommandPort
import com.jeffreyoh.userservice.storage.adapter.outbound.redis.RedisReadAdapter.Companion.getRecentKeywordKey
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class RedisCommandAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): RedisCommandPort {

    companion object {
        private const val RECENT_KEYWORD_LIMIT = 10
        private const val TTL_HOURS = 1L
    }

    override fun saveRecentKeyword(userId: Long, keyword: String): Mono<Void> {
        val key = getRecentKeywordKey(userId)
        return redisTemplate.opsForList()
            .remove(key, 0, keyword)
            .then(redisTemplate.opsForList().leftPush(key, keyword))
            .then(redisTemplate.opsForList().trim(key, 0, RECENT_KEYWORD_LIMIT - 1L))
            .then(redisTemplate.expire(key, Duration.ofHours(TTL_HOURS)))
            .then()

    }

}