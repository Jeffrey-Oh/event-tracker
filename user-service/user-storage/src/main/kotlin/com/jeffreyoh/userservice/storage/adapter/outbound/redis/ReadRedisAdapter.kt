package com.jeffreyoh.userservice.storage.adapter.redis

import com.jeffreyoh.userservice.port.out.ReadRedisPort
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class ReadRedisAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
): ReadRedisPort {

    companion object {
        private const val KEY = "recent:search:user:%d"
    }

    private fun getKey(userId: Long) = KEY.format(userId)

    override fun recentSearchByKeyword(userId: Long): Mono<List<String>> {
        return redisTemplate.opsForList().range(getKey(userId), 0, -1).collectList()
    }

}