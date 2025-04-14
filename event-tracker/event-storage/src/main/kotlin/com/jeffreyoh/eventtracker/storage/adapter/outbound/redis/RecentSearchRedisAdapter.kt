package com.jeffreyoh.eventtracker.storage.adapter.outbound.redis

import com.jeffreyoh.eventtracker.application.port.out.RecentSearchRedisPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

private val log = KotlinLogging.logger {}

@Component
class RecentSearchRedisAdapter(
    private val redisTemplate: ReactiveStringRedisTemplate
) : RecentSearchRedisPort {

    companion object {
        private const val RECENT_KEYWORD_LIMIT = 10
        private const val TTL_HOURS = 1L
        private const val KEY = "recent:search:user:%d"
    }

    private fun getKey(userId: Long): String = KEY.format(userId)

    override fun saveRecentKeyword(
        userId: Long,
        keyword: String
    ): Mono<Void> {
        log.info { "saveRecentKeyword: userId=$userId, keyword=$keyword" }
        // LREM - 중복 제거
        // LPUSH - 리스트 앞에 추가
        // LTRIM - 리스트 크기 제한
        // EXPIRE - TTL 설정
        val key = getKey(userId)
        return redisTemplate.opsForList()
            .remove(key, 0, keyword)
            .then(redisTemplate.opsForList().leftPush(key, keyword))
            .then(redisTemplate.opsForList().trim(key, 0, RECENT_KEYWORD_LIMIT - 1L))
            .then(redisTemplate.expire(key, Duration.ofHours(TTL_HOURS)))
            .then()

    }

    override fun getRecentKeywords(userId: Long): Mono<List<String>> {
        return redisTemplate.opsForList().range(getKey(userId), 0, -1).collectList()
    }

}