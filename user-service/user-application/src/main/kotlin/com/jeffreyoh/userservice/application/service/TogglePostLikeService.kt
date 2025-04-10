package com.jeffreyoh.userservice.application.service

import com.jeffreyoh.userservice.core.domain.EventTrackerCommand
import com.jeffreyoh.userservice.core.domain.PostLike
import com.jeffreyoh.userservice.core.domain.PostLikeCommand
import com.jeffreyoh.userservice.port.`in`.TogglePostLikeUseCase
import com.jeffreyoh.userservice.port.out.EventTrackerPort
import com.jeffreyoh.userservice.port.out.PostLikeCommandPort
import reactor.core.publisher.Mono

class TogglePostLikeService(
    private val postLikeCommandPort: PostLikeCommandPort,
    private val eventTrackerPort: EventTrackerPort
) : TogglePostLikeUseCase {

    override fun toggle(command: PostLikeCommand.PostLikeCommand): Mono<Void> {
        return postLikeCommandPort.findByUserIdAndPostId(command.userId, command.postId)
            .flatMap { postLikeCommandPort.delete(it.postLikeId).thenReturn(false) }
            .switchIfEmpty(postLikeCommandPort.save(command.toPostLike()).thenReturn(true))
            .then(
                eventTrackerPort.sendEvent(
                    EventTrackerCommand.PayloadCommand(
                        EventTrackerCommand.EventType.LIKE,
                        command.userId,
                        command.postId
                    )
                )
            )
    }

}