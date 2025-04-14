package com.jeffreyoh.userservice.storage.adapter.outbound.redis

import com.jeffreyoh.userservice.application.port.out.RedisReadPort
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RedisReadAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): RedisReadPort {

    companion object {
        private const val RECENT_KEYWORD_KEY = "recent:search:user:%d"

        fun getRecentKeywordKey(userId: Long) = RECENT_KEYWORD_KEY.format(userId)
    }

    override fun recentSearchByKeyword(userId: Long): Mono<List<String>> {
        return redisTemplate.opsForList().range(getRecentKeywordKey(userId), 0, -1).collectList()
    }

}