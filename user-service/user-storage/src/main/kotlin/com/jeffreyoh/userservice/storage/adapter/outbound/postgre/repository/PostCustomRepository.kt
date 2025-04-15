package com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository

import com.jeffreyoh.userservice.application.model.post.SearchKeywordSaveRedisByLikeResult
import reactor.core.publisher.Flux

interface PostCustomRepository {

    fun searchByKeyword(keyword: String, limit: Int): Flux<SearchKeywordSaveRedisByLikeResult>

}