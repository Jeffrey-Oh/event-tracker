package com.jeffreyoh.userservice.application.service

import com.jeffreyoh.userservice.core.command.EventTrackerCommand
import com.jeffreyoh.userservice.core.command.EventTrackerCommand.EventMetadata
import com.jeffreyoh.userservice.core.domain.EventType
import com.jeffreyoh.userservice.core.command.PostLikeCommand
import com.jeffreyoh.userservice.port.`in`.TogglePostLikeUseCase
import com.jeffreyoh.userservice.port.out.EventTrackerPort
import com.jeffreyoh.userservice.port.out.PostLikeCommandPort
import reactor.core.publisher.Mono
import java.util.UUID

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
                    EventTrackerCommand.SaveEventCommand(
                        EventType.LIKE,
                        command.userId,
                        UUID.randomUUID().toString(),
                        EventMetadata(
                            componentId = EventType.LIKE.componentId,
                            elementId = "elementId-${EventType.LIKE.groupId}",
                            keyword = null,
                            postId = command.postId
                        )
                    )
                )
            )
    }

}