package com.jeffreyoh.userservice.storage.adapter.postgre.repository.impl

import com.jeffreyoh.userservice.storage.adapter.postgre.repository.PostLikeCustomRepository
import com.jeffreyoh.userservice.storage.entity.PostLikeEntity
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Repository
class PostLikeCustomRepositoryImpl(
    private val databaseClient: DatabaseClient
) : PostLikeCustomRepository {

    override fun save(postLikeEntity: PostLikeEntity): Mono<PostLikeEntity> {
        return databaseClient.sql(
            """
            INSERT INTO post_like (user_id, post_id, liked_at)
            VALUES (:userId, :postId, :likedAt)
            ON CONFLICT (user_id, post_id) DO NOTHING
            RETURNING post_like_id, user_id, post_id, liked_at
            """.trimIndent()
        )
            .bind("userId", postLikeEntity.userId)
            .bind("postId", postLikeEntity.postId)
            .bind("likedAt", postLikeEntity.likedAt)
            .map { row, _ ->
                PostLikeEntity(
                    postLikeId = row.get("post_like_id", Long::class.java)!!,
                    userId = row.get("user_id", Long::class.java)!!,
                    postId = row.get("post_id", Long::class.java)!!,
                    likedAt = row.get("liked_at", LocalDateTime::class.java)!!
                )
            }
            .one()
            .switchIfEmpty(Mono.empty()) // 충돌이 발생해서 삽입 안 되었을 경우
    }

}