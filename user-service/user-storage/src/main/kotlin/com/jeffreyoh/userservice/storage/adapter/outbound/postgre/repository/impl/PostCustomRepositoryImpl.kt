package com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository.impl

import com.jeffreyoh.userservice.application.model.post.SearchKeywordSaveRedisByLikeResult
import com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository.PostCustomRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Repository
class PostCustomRepositoryImpl(
    private val databaseClient: DatabaseClient
) : PostCustomRepository {

    override fun searchByKeyword(
        keyword: String,
        limit: Int
    ): Flux<SearchKeywordSaveRedisByLikeResult> {
        val startTime = System.nanoTime()

        return databaseClient.sql(
                """
                    SELECT p.*, COUNT(pl.post_id) AS like_count
                    FROM post p
                    JOIN post_like pl ON pl.post_id = p.post_id
                    WHERE p.hashtags @> ARRAY[:keyword]::TEXT[]
                    GROUP BY p.post_id
                    ORDER BY like_count DESC
                    LIMIT :limit
                """
            )
            .bind("keyword", keyword)
            .bind("limit", limit)
            .map { row ->
                SearchKeywordSaveRedisByLikeResult(
                    postId = row.get("post_id", Long::class.java)!!,
                    userId = row.get("user_id", Long::class.java)!!,
                    content = row.get("content", String::class.java)!!,
                    imageUrls = row.get("image_urls", String::class.java),
                    hashtags = row.get("hashtags", Array<String>::class.java)?.toList() ?: emptyList(),
                    createdAt = row.get("created_at", LocalDateTime::class.java)!!,
                    likeCount = row.get("like_count", Long::class.java) ?: 0L
                )
            }
            .all()
            .doOnNext { log.debug { "게시물 검색 성공: keyword=$keyword" } }
            .doOnError { e -> log.error { "게시물 검색 실패: keyword=$keyword, error=${e.message}" } }
            .doFinally {
                val durationMs = (System.nanoTime() - startTime) / 1_000_000
                log.info { "게시물 검색 시간: ${durationMs}ms for keyword=$keyword" }
            }
            .onErrorResume { e ->
                log.warn { "DB 조회 실패, 빈 결과 반환: keyword=$keyword" }
                Flux.empty()
            }
    }
}