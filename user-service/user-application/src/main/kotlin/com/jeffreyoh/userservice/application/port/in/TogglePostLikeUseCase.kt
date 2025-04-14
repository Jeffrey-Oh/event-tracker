package com.jeffreyoh.userservice.application.port.`in`

import com.jeffreyoh.userservice.application.model.post.PostLikeCommand
import reactor.core.publisher.Mono

interface TogglePostLikeUseCase {

    fun toggle(command: PostLikeCommand.TogglePostLike): Mono<Void>

}