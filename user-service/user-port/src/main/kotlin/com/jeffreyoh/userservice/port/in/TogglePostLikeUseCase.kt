package com.jeffreyoh.userservice.port.`in`

import com.jeffreyoh.userservice.core.command.PostLikeCommand
import reactor.core.publisher.Mono

interface TogglePostLikeUseCase {

    fun toggle(command: PostLikeCommand.PostLikeCommand): Mono<Void>

}