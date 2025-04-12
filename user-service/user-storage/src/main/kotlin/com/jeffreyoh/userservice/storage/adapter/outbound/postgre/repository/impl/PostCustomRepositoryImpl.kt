package com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository.impl

import com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository.PostCustomRepository
import com.jeffreyoh.userservice.storage.entity.PostEntity
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Repository
class PostCustomRepositoryImpl(
    private val databaseClient: DatabaseClient
) : PostCustomRepository {

    override fun searchByKeyword(
        keyword: String,
        limit: Int
    ): Flux<PostEntity> {
        val query =
            """
            SELECT * FROM post 
            WHERE content ILIKE '%' || :keyword || '%' 
               OR :keyword = ANY(hashtags)
            LIMIT :limit
            """.trimIndent()

        return databaseClient.sql(query)
            .bind("keyword", keyword)
            .bind("limit", limit)
            .map { row, _ ->
                PostEntity(
                    postId = row.get("post_id", Long::class.java)!!,
                    userId = row.get("user_id", Long::class.java)!!,
                    content = row.get("content", String::class.java)!!,
                    imageUrls = row.get("image_urls", String::class.java),
                    hashtags = row.get("hashtags", Array<String>::class.java)?.toList() ?: emptyList(),
                    createdAt = row.get("created_at", LocalDateTime::class.java)!!
                )
            }
            .all()
    }

}