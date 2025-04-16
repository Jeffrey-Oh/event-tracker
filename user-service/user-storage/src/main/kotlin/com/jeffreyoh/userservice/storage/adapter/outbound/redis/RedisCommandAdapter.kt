package com.jeffreyoh.userservice.storage.adapter.outbound.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.jeffreyoh.userservice.application.model.post.SearchKeywordSaveRedisByLikeResult
import com.jeffreyoh.userservice.application.port.out.RedisCommandPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.random.Random

private val log = KotlinLogging.logger {}

@Component
class RedisCommandAdapter(
    private val objectMapper: ObjectMapper,
    private val redisTemplate: ReactiveStringRedisTemplate
) : RedisCommandPort {

    companion object {
        private const val RECENT_KEYWORD_LIMIT = 10
        private const val RECENT_KEYWORD_TTL_HOURS = 1L
        private const val SEARCH_RESULT_TTL_SECONDS = 60L
        private const val SEARCH_KEYWORD_PREFIX = "search:keyword"
        private const val RECENT_KEYWORD_PREFIX = "recent:search:user"
        private const val EMPTY_PLACEHOLDER = "__EMPTY__"

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

    override fun cacheSearchResults(keyword: String, posts: Flux<SearchKeywordSaveRedisByLikeResult>): Mono<Void> {
        val redisKey = getSearchKeywordKey(keyword)
        val jitter = Random.nextLong(0, 10)
        val ttlWithJitter = SEARCH_RESULT_TTL_SECONDS + jitter

        return posts
            .take(10) // 최대 10개만 저장
            .map { post -> objectMapper.writeValueAsString(post.copy(updatedAt = null)) }
            .collectList()
            .flatMap { jsonList ->
                if (jsonList.isEmpty()) {
                    log.debug { "🔍 캐시 저장할 결과 없음: keyword=$keyword" }
                    return@flatMap Mono.empty()
                }

                redisTemplate.opsForList()
                    .leftPushAll(redisKey, jsonList)
                    .then(redisTemplate.expire(redisKey, Duration.ofSeconds(ttlWithJitter)))
                    .then(redisTemplate.opsForList().trim(redisKey, 0, RECENT_KEYWORD_LIMIT - 1L))
                    .doOnSuccess { log.debug { "📦 검색 결과 캐싱 성공: keyword=$keyword, size=${jsonList.size}" } }
                    .doOnError { e -> log.error { "❌ 검색 결과 캐싱 실패: keyword=$keyword, error=${e.message}" } }
                    .then()
            }
            .onErrorResume {
                log.warn { "⚠️ 캐싱 실패 무시 (데이터 손실 방지): keyword=$keyword" }
                Mono.empty()
            }
    }

    override fun cacheEmptySearchResult(keyword: String): Mono<Void> {
        val redisKey = getSearchKeywordKey(keyword)
        val baseTtl = 30L
        val jitter = Random.nextLong(0, 10)
        val ttlWithJitter = baseTtl + jitter

        return redisTemplate.opsForList()
            .leftPush(redisKey, EMPTY_PLACEHOLDER)
            .then(redisTemplate.expire(redisKey, Duration.ofSeconds(ttlWithJitter)))
            .then(redisTemplate.opsForList().trim(redisKey, 0, 0)) // 1개만 유지
            .doOnSuccess { log.debug { "빈 검색 결과 캐시 성공: keyword=$keyword" } }
            .onErrorResume {
                log.warn { "빈 검색 결과 캐싱 실패: keyword=$keyword, error=${it.message}" }
                Mono.empty()
            }
            .then()
    }

}