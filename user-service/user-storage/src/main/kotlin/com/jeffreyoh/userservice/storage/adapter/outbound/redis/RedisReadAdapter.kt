package com.jeffreyoh.userservice.storage.adapter.outbound.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.jeffreyoh.userservice.application.model.post.SearchKeywordSaveRedisByLikeResult
import com.jeffreyoh.userservice.application.port.out.RedisReadPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Component
class RedisReadAdapter(
    private val objectMapper: ObjectMapper,
    private val redisTemplate: ReactiveStringRedisTemplate
) : RedisReadPort {

    companion object {
        private const val RECENT_KEYWORD_PREFIX = "recent:search:user"
        private const val SEARCH_KEYWORD_PREFIX = "search:keyword"

        fun getRecentKeywordKey(userId: Long) = "$RECENT_KEYWORD_PREFIX:$userId"
        fun getSearchKeywordKey(keyword: String) = "$SEARCH_KEYWORD_PREFIX:$keyword"
    }

    override fun recentSearchByKeyword(userId: Long): Mono<List<String>> {
        val key = getRecentKeywordKey(userId)
        return redisTemplate.opsForList()
            .range(key, 0, -1)
            .collectList()
            .doOnSuccess { keywords ->
                log.debug { "최근 키워드 조회 성공: userId=$userId, count=${keywords.size}" }
            }
            .doOnError { e ->
                log.error { "최근 키워드 조회 실패: userId=$userId, error=${e.message}" }
            }
    }

    override fun getCachedSearchResults(keyword: String): Flux<SearchKeywordSaveRedisByLikeResult> {
        val redisKey = getSearchKeywordKey(keyword)
        return redisTemplate.opsForList()
            .range(redisKey, 0, 9)
            .flatMap { json ->
                try {
                    val post = objectMapper.readValue(json, SearchKeywordSaveRedisByLikeResult::class.java)
                    Mono.just(post)
                } catch (e: Exception) {
                    log.warn { "캐시 역직렬화 실패: keyword=$keyword, error=${e.message}" }
                    Mono.empty()
                }
            }
            .doOnNext { log.debug { "캐시 히트: keyword=$keyword" } }
            .doOnComplete { log.debug { "캐시 조회 완료: keyword=$keyword" } }
            .doOnError { e ->
                log.error { "캐시 조회 실패: keyword=$keyword, error=${e.message}" }
            }
    }

}