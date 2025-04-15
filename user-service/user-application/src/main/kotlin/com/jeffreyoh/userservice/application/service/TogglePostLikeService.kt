package com.jeffreyoh.userservice.application.service

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.userservice.application.model.event.EventTrackerRequest
import com.jeffreyoh.userservice.application.model.post.PostLikeCommand
import com.jeffreyoh.userservice.application.port.`in`.TogglePostLikeUseCase
import com.jeffreyoh.userservice.application.port.out.EventTrackerPort
import com.jeffreyoh.userservice.application.port.out.PostLikeCommandPort
import com.jeffreyoh.userservice.core.domain.event.EventMetadata
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

class TogglePostLikeService(
    private val postLikeCommandPort: PostLikeCommandPort,
    private val eventTrackerPort: EventTrackerPort
) : TogglePostLikeUseCase {

    override fun toggle(command: PostLikeCommand.TogglePostLike): Mono<Void> {
        return getLikeStatus(command.userId, command.postId)
            .flatMap { likeExists ->
                if (likeExists) {
                    handleUnlike(command)
                } else {
                    handleLike(command)
                }
            }
    }

    private fun getLikeStatus(userId: Long, postId: Long): Mono<Boolean> {
        return postLikeCommandPort.findByUserIdAndPostId(userId, postId)
            .map { true }
            .defaultIfEmpty(false)
    }

    private fun handleUnlike(command: PostLikeCommand.TogglePostLike): Mono<Void> {
        return postLikeCommandPort.delete(command.userId, command.postId)
            .doOnSuccess {
                sendLikeEvent(command, EventType.UNLIKE)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe()
            }
            .then()
    }

    private fun handleLike(command: PostLikeCommand.TogglePostLike): Mono<Void> {
        return postLikeCommandPort.save(command.toPostLike())
            .doOnSuccess {
                sendLikeEvent(command, EventType.LIKE)
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe()
            }
            .then()
    }

    private fun sendLikeEvent(command: PostLikeCommand.TogglePostLike, eventType: EventType): Mono<Void> {
        return eventTrackerPort.sendEvent(
            EventTrackerRequest.SaveEvent(
                eventType = eventType,
                userId = command.userId,
                sessionId = UUID.randomUUID().toString(),
                metadata = EventMetadata(
                    componentId = eventType.componentId,
                    elementId = "elementId-${eventType.groupId}",
                    keyword = null,
                    postId = command.postId
                )
            )
        )
    }


}