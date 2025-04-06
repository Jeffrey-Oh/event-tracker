package com.jeffreyoh.eventtracker.application.service

import com.jeffreyoh.eventtracker.core.domain.event.EventCommand
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.port.input.SaveEventUseCase
import com.jeffreyoh.eventtracker.port.output.EventRedisPort
import com.jeffreyoh.eventtracker.port.output.StatisticsRedisPort
import io.github.oshai.kotlinlogging.KotlinLogging
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

class SaveEventService(
    private val eventRedisPort: EventRedisPort,
    private val statisticsRedisPort: StatisticsRedisPort,
): SaveEventUseCase {

    private fun keyPostLike(userId: Long, postId: Long): String
        = "events:${EventType.LIKE.name.lowercase()}:user:$userId:post:$postId"

    override fun saveEvent(command: EventCommand.SaveEventCommand): Mono<Void> {
        log.debug { "UseCase handle called: $command" }
        val event = command.toEvent()

        return if (event.eventType == EventType.LIKE) {
            val key = keyPostLike(command.userId!!, command.metadata.postId!!)

            eventRedisPort.readLikeFromRedisKey(key)
                .defaultIfEmpty("MISSING")
                .flatMap { cachedEvent ->
                    if (cachedEvent == "MISSING") {
                        val likeEvent = event.copy(eventType = EventType.LIKE)
                        log.info { "LIKE 이벤트 저장" }
                        eventRedisPort.saveLikeEventToRedis(key, likeEvent)
                            .then(statisticsRedisPort.incrementLike(likeEvent.metadata.componentId, likeEvent.metadata.postId!!))
                    } else {
                        val unLikeEvent = event.copy(eventType = EventType.UNLIKE)
                        log.info { "UNLIKE 이벤트 저장" }
                        eventRedisPort.deleteFromRedisKey(key)
                            .then(statisticsRedisPort.decrementLike(unLikeEvent.metadata.componentId, unLikeEvent.metadata.postId!!))
                    }
                }
        } else {
            eventRedisPort.saveToRedis(event)
                .then(
                    when(event.eventType) {
                        EventType.CLICK -> statisticsRedisPort.incrementClick(event.metadata.componentId)
                        EventType.PAGE_VIEW -> statisticsRedisPort.incrementPageView(event.metadata.componentId)
                        EventType.SEARCH -> statisticsRedisPort.incrementSearch(event.metadata.componentId)
                        else -> Mono.empty()
                    }
                )
        }
    }

}