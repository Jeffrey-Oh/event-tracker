package com.jeffreyoh.userservice.application.port.out

import com.jeffreyoh.userservice.application.model.post.SearchKeywordSaveRedisByLikeResult
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RedisReadPort {

    fun recentSearchByKeyword(userId: Long): Mono<List<String>>
    fun getCachedSearchResults(keyword: String): Flux<SearchKeywordSaveRedisByLikeResult>

}