package com.jeffreyoh.userservice.api.adapter.inbound.web.controller

import com.jeffreyoh.userservice.api.adapter.inbound.web.dto.PostDTO
import com.jeffreyoh.userservice.port.`in`.CreatePostUseCase
import com.jeffreyoh.userservice.port.`in`.TogglePostLikeUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val createPostUseCase: CreatePostUseCase,
    private val togglePostLikeUseCase: TogglePostLikeUseCase
) {

    @PostMapping
    fun createPost(
        @RequestBody request: PostDTO.CreatePostRequest
    ): Mono<Void> {
        return createPostUseCase.createPost(request.toCommand())
    }

    @PostMapping("/like")
    fun toggleLike(
        @RequestBody request: PostDTO.ToggleRequest
    ): Mono<Void> {
        return togglePostLikeUseCase.toggle(request.toCommand())
    }

}