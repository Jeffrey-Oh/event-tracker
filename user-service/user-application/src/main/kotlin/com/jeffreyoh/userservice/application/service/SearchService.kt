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
import java.util.*
import kotlin.random.Random

private val log = KotlinLogging.logger {}

class SearchService(
    private val eventTrackerPort: EventTrackerPort,
    private val postSearchPort: PostSearchPort,
    private val redisReadPort: RedisReadPort,
    private val commandRedisPort: RedisCommandPort,
    private val distributedLockPort: DistributedLockPort
) : SearchUseCase {

    override fun searchByKeyword(
        userId: Long,
        keyword: String
    ): Flux<SearchKeywordSaveRedisByLikeResult> {
        val startTime = System.nanoTime()

        saveRecentKeywordAndTrack(userId, keyword)

        val waitTimeSec = 5L + Random.nextLong(0, 500) / 1000L

        return redisReadPort.getCachedSearchResults(keyword)
            .switchIfEmpty(
                distributedLockPort.withLock(
                    key = "search_lock:keyword:$keyword",
                    waitTimeSec = waitTimeSec,
                    leaseTimeSec = 5
                ) {
                    log.debug { "🔒 락 블록 실행: keyword=$keyword" }
                    val searchByKeyword = postSearchPort.searchByKeyword(keyword).cache()
                    searchByKeyword
                        .hasElements()
                        .flatMap { hasElements ->
                            if (hasElements) {
                                log.debug { "🔍 DB 결과 캐싱: keyword=$keyword" }
                                commandRedisPort.cacheSearchResults(keyword, searchByKeyword)
                                    .then(Mono.just(searchByKeyword))
                            } else {
                                log.debug { "🔍 DB 결과 없음, 빈 캐시 저장: keyword=$keyword" }
                                commandRedisPort.cacheEmptySearchResult(keyword)
                                    .then(Mono.just(Flux.empty()))
                            }
                        }
                }
                .flatMapMany { it }
                .onErrorResume { e ->
                    log.warn(e) { "🚨 락 실패, Redis 임시 캐시 폴백: keyword=$keyword, waitTimeSec=$waitTimeSec" }

                    redisReadPort.getCachedSearchResults(keyword)
                        .switchIfEmpty(
                            Flux.defer {
                                log.debug { "🔍 DB 폴백 시작: keyword=$keyword" }
                                val searchByKeyword = postSearchPort.searchByKeyword(keyword).cache()
                                searchByKeyword
                                    .hasElements()
                                    .flatMap { hasElements ->
                                        if (hasElements) {
                                            log.debug { "🔍 DB 결과 캐싱: keyword=$keyword" }
                                            commandRedisPort.cacheSearchResults(keyword, searchByKeyword)
                                                .thenReturn(searchByKeyword)
                                        } else {
                                            log.debug { "🔍 DB 결과 없음, 빈 캐시 저장: keyword=$keyword" }
                                            commandRedisPort.cacheEmptySearchResult(keyword)
                                                .thenReturn(Flux.empty())
                                        }
                                    }
                                    .flatMapMany { it }
                                    .onErrorResume { dbError ->
                                        log.warn(dbError) { "🔍 DB 폴백 실패: ${dbError.message}, keyword=$keyword" }
                                        Flux.empty()
                                    }
                            }
                        )
                }
            )
            .doOnError { e -> log.error(e) { "❌ 검색 처리 실패: ${e.message}, keyword=$keyword, userId=$userId" } }
            .doFinally {
                val durationMs = (System.nanoTime() - startTime) / 1_000_000
                log.info { "🔍 검색 처리 시간: ${durationMs}ms for keyword=$keyword, userId=$userId" }
            }
    }

    private fun saveRecentKeywordAndTrack(userId: Long, keyword: String) {
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

        commandRedisPort.saveRecentKeyword(userId, keyword)
            .then(
                eventTrackerPort.sendEvent(eventRequest)
                    .doOnError { e ->
                        log.error(e) { "⚠️ 이벤트 전송 실패: ${e.message}, userId=$userId, keyword=$keyword" }
                    }
                    .onErrorResume { Mono.empty() }
            )
            .doOnError { e ->
                log.error(e) { "❌ Redis 저장 실패: ${e.message}, userId=$userId, keyword=$keyword" }
            }
            .onErrorResume { Mono.empty() }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    override fun recentSearchByKeyword(userId: Long): Mono<List<String>> {
        return redisReadPort.recentSearchByKeyword(userId)
    }

}