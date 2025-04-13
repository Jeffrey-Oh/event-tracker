package com.jeffreyoh.userservice.storage.adapter.outbound.redis

import com.jeffreyoh.userservice.port.out.ReadRedisPort
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RedisReadAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): ReadRedisPort {

    companion object {
        private const val RECENT_KEYWORD_KEY = "recent:search:user:%d"
        private const val LIKE_CHECK_KEY = "like:user:%d:post:%d"

        fun getRecentKeywordKey(userId: Long) = RECENT_KEYWORD_KEY.format(userId)
        fun getLikeCheckKey(userId: Long, postId: Long) = LIKE_CHECK_KEY.format(userId, postId)
    }

    override fun getLikeCheck(userId: Long, postId: Long): Mono<Boolean> {
        return redisTemplate.opsForValue()
            .get(getLikeCheckKey(userId, postId))
            .map { it.toBoolean() }
    }

    override fun recentSearchByKeyword(userId: Long): Mono<List<String>> {
        return redisTemplate.opsForList().range(getRecentKeywordKey(userId), 0, -1).collectList()
    }

}