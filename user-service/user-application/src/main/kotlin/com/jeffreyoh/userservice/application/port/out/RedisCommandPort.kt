package com.jeffreyoh.userservice.application.port.out

import com.jeffreyoh.userservice.application.model.post.SearchKeywordSaveRedisByLikeResult
import reactor.core.publisher.Mono

interface RedisCommandPort {

    fun saveRecentKeyword(userId: Long, keyword: String): Mono<Void>
    fun cacheSearchResults(keyword: String, post: SearchKeywordSaveRedisByLikeResult): Mono<Void>

}