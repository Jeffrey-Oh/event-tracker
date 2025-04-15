package com.jeffreyoh.userservice.application.service

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.userservice.application.model.event.EventTrackerRequest
import com.jeffreyoh.userservice.application.model.post.SearchKeywordSaveRedisByLikeResult
import com.jeffreyoh.userservice.application.port.`in`.SearchUseCase
import com.jeffreyoh.userservice.application.port.out.*
import com.jeffreyoh.userservice.core.domain.event.EventMetadata
import io.github.oshai.kotlinlogging.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.retry.Retry
import java.time.Duration
import java.util.*

class SearchService(
    private val eventTrackerPort: EventTrackerPort,
    private val postSearchPort: PostSearchPort,
    private val redisReadPort: RedisReadPort,
    private val commandRedisPort: RedisCommandPort,
    private val distributedLockPort: DistributedLockPort
) : SearchUseCase {
    private val logger = KotlinLogging.logger {}

    override fun searchByKeyword(
        userId: Long,
        keyword: String
    ): Flux<SearchKeywordSaveRedisByLikeResult> {
        val startTime = System.nanoTime()

        // 이벤트 추적 비동기 처리
        saveRecentKeywordAndTrack(userId, keyword)
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

        return redisReadPort.getCachedSearchResults(keyword)
            .switchIfEmpty(
                distributedLockPort.withLockReactive(
                    key = "search_lock:keyword:$keyword",
                    waitTimeSec = 2,
                    leaseTimeSec = 3
                ) {
                    Mono.just(
                        postSearchPort.searchByKeyword(keyword)
                            .flatMap { post ->
                                commandRedisPort.cacheSearchResults(keyword, post)
                                    .thenReturn(post)
                            }
                    )
                }
                    .flatMapMany { it }
                    .onErrorResume { e ->
                        logger.warn { "🚨 락 실패, DB 폴백: ${e.message}" }
                        postSearchPort.searchByKeyword(keyword)
                            .flatMap { post ->
                                commandRedisPort.cacheSearchResults(keyword, post)
                                    .thenReturn(post)
                            }
                            .onErrorResume { dbError ->
                                logger.error { "❌ DB 조회 실패: ${dbError.message}" }
                                Flux.empty()
                            }
                    }
            )
            .doOnNext { logger.info { "캐시 히트: keyword=$keyword" } }
            .doOnError { e -> logger.error { "❌ 검색 처리 실패: ${e.message}" } }
            .doFinally {
                val durationMs = (System.nanoTime() - startTime) / 1_000_000
                logger.info { "🔍 검색 처리 시간: ${durationMs}ms for keyword=$keyword" }
            }
    }

    private fun saveRecentKeywordAndTrack(userId: Long, keyword: String): Mono<Void> {
        val eventRequest = EventTrackerRequest.SaveEvent(
            eventType = EventType.SEARCH,
            userId = userId,
            sessionId = UUID.randomUUID().toString(),
            metadata = EventMetadata(
                componentId = EventType.SEARCH.componentId,
                elementId = "elementId-${EventType.SEARCH.groupId}",
                keyword = keyword,
                postId = null
            )
        )

        return Mono.zip(
            commandRedisPort.saveRecentKeyword(userId, keyword),
            eventTrackerPort.sendEvent(eventRequest)
        )
            .doOnError { e -> logger.error { "❌ 이벤트/키워드 저장 실패: ${e.message}" } }
            .retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
            .then()
            .subscribeOn(Schedulers.boundedElastic())
    }

    override fun recentSearchByKeyword(userId: Long): Mono<List<String>> {
        return redisReadPort.recentSearchByKeyword(userId)
    }
}