package com.jeffreyoh.userservice.storage.adapter.outbound.redis

import com.jeffreyoh.userservice.port.out.CommandRedisPort
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class RedisCommandAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): CommandRedisPort {

    companion object {
        private const val LIKE_CHECK_KEY = "like:user:%d:post:%d"

        fun saveLikeCheckKey(userId: Long, postId: Long) = LIKE_CHECK_KEY.format(userId, postId)
    }

    override fun saveLikeCheck(userId: Long, postId: Long): Mono<Void> {
        return redisTemplate.opsForValue()
            .set(saveLikeCheckKey(userId, postId), "true")
            .then()
    }

    override fun deleteLikeCheck(userId: Long, postId: Long): Mono<Void> {
        return redisTemplate.delete(saveLikeCheckKey(userId, postId))
            .then()
    }
}