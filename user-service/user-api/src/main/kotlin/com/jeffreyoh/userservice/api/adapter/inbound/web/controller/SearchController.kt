package com.jeffreyoh.userservice.api.adapter.inbound.web.controller

import com.jeffreyoh.userservice.api.adapter.inbound.web.dto.PostDTO
import com.jeffreyoh.userservice.api.adapter.inbound.web.dto.SearchDTO
import com.jeffreyoh.userservice.port.`in`.SearchUseCase
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/search")
class SearchController(
    private val searchUseCase: SearchUseCase,
) {

    @GetMapping
    fun searchByKeyword(
        @RequestParam userId: Long,
        @RequestParam keyword: String
    ): Flux<PostDTO.PostResponse> {
        return searchUseCase.searchByKeyword(userId, keyword)
            .map { PostDTO.PostResponse.fromDomain(it) }
    }

    @GetMapping("/recent/{userId}")
    fun recentSearchByKeyword(
        @PathVariable userId: Long
    ): Mono<SearchDTO.RecentSearchByKeywordResponse> {
        return searchUseCase.recentSearchByKeyword(userId)
            .map { SearchDTO.RecentSearchByKeywordResponse(it) }
    }

}