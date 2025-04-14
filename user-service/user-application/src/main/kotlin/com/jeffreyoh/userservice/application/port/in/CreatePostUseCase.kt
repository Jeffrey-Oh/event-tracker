package com.jeffreyoh.userservice.application.port.`in`

import com.jeffreyoh.userservice.application.model.post.PostCommand
import reactor.core.publisher.Mono

interface CreatePostUseCase {

    fun createPost(command: PostCommand.CreatePostCommand): Mono<Void>

}