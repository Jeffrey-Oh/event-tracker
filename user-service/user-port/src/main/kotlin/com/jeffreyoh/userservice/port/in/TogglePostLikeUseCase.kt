package com.jeffreyoh.userservice.port.`in`

import com.jeffreyoh.userservice.core.domain.PostLikeCommand
import reactor.core.publisher.Mono

interface TogglePostLikeUseCase {

    fun toggle(command: PostLikeCommand.PostLikeCommand): Mono<Void>

}