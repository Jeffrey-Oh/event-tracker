package com.jeffreyoh.userservice.api.adapter.inbound.web.controller

import com.jeffreyoh.userservice.api.adapter.inbound.web.dto.PostDTO
import com.jeffreyoh.userservice.port.`in`.SearchUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

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

}