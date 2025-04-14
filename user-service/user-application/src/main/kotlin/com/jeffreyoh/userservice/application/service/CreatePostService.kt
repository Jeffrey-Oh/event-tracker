package com.jeffreyoh.userservice.application.service

import com.jeffreyoh.userservice.application.model.post.PostCommand
import com.jeffreyoh.userservice.application.port.`in`.CreatePostUseCase
import com.jeffreyoh.userservice.port.out.PostCommandPort
import reactor.core.publisher.Mono

class CreatePostService(
    private val postCommandPort: PostCommandPort,
): CreatePostUseCase {

    override fun createPost(command: PostCommand.CreatePostCommand): Mono<Void> {
        return postCommandPort.save(command.toPost())
            .then()
    }

}