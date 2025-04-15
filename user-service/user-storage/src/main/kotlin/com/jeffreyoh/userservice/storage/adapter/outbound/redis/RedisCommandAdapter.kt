package com.jeffreyoh.userservice.storage.adapter.outbound.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.jeffreyoh.userservice.application.model.post.SearchKeywordSaveRedisByLikeResult
import com.jeffreyoh.userservice.application.port.out.RedisCommandPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

private val log = KotlinLogging.logger {}

@Component
class RedisCommandAdapter(
    private val objectMapper: ObjectMapper,
    private val redisTemplate: ReactiveStringRedisTemplate
) : RedisCommandPort {

    companion object {
        private const val RECENT_KEYWORD_LIMIT = 10
        private const val RECENT_KEYWORD_TTL_HOURS = 1L
        private const val SEARCH_RESULT_TTL_MINUTES = 1L
        private const val SEARCH_KEYWORD_PREFIX = "search:keyword"
        private const val RECENT_KEYWORD_PREFIX = "recent:search:user"

        fun getSearchKeywordKey(keyword: String) = "$SEARCH_KEYWORD_PREFIX:$keyword"
        fun getRecentKeywordKey(userId: Long) = "$RECENT_KEYWORD_PREFIX:$userId"
    }

    override fun saveRecentKeyword(userId: Long, keyword: String): Mono<Void> {
        val key = getRecentKeywordKey(userId)
        return redisTemplate.opsForList()
            .remove(key, 0, keyword)
            .then(redisTemplate.opsForList().leftPush(key, keyword))
            .then(redisTemplate.opsForList().trim(key, 0, RECENT_KEYWORD_LIMIT - 1L))
            .then(redisTemplate.expire(key, Duration.ofHours(RECENT_KEYWORD_TTL_HOURS)))
            .doOnSuccess { log.debug { "최근 키워드 저장 성공: userId=$userId, keyword=$keyword" } }
            .doOnError { e -> log.error { "최근 키워드 저장 실패: userId=$userId, error=${e.message}" } }
            .then()
    }

    override fun cacheSearchResults(keyword: String, post: SearchKeywordSaveRedisByLikeResult): Mono<Void> {
        val redisKey = getSearchKeywordKey(keyword)
        val json = objectMapper.writeValueAsString(post.copy(updatedAt = null))

        return redisTemplate.opsForList()
            .leftPush(redisKey, json)
            .then(redisTemplate.expire(redisKey, Duration.ofMinutes(SEARCH_RESULT_TTL_MINUTES)))
            .then(redisTemplate.opsForList().trim(redisKey, 0, 9))
            .doOnSuccess { log.debug { "단일 검색 결과 캐싱 성공: keyword=$keyword" } }
            .doOnError { e -> log.error { "단일 검색 결과 캐싱 실패: keyword=$keyword, error=${e.message}" } }
            .then()
            .onErrorResume {
                log.warn { "캐싱 실패 무시, 데이터 손실 방지: keyword=$keyword" }
                Mono.empty()
            }
    }

}