package com.jeffreyoh.userservice.port.`in`

import com.jeffreyoh.userservice.core.command.PostCommand
import reactor.core.publisher.Mono

interface CreatePostUseCase {

    fun createPost(command: PostCommand.CreatePostCommand): Mono<Void>

}