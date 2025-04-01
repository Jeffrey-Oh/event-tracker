package com.jeffreyoh.eventapplication.service

import com.jeffreyoh.eventcore.domain.event.EventCommand
import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.input.SaveEventUseCase
import com.jeffreyoh.eventport.output.EventRedisPort
import com.jeffreyoh.eventport.output.StatisticsRedisPort
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
                        log.info { "Event does not exist in Redis, saving it" }
                        eventRedisPort.saveLikeEventToRedis(key, event)
                            .then(statisticsRedisPort.incrementLike(event.metadata.componentId, event.metadata.postId!!))
                    } else {
                        log.info { "Event already exists in Redis, deleting it" }
                        eventRedisPort.deleteFromRedisKey(key)
                            .then(statisticsRedisPort.decrementLike(event.metadata.componentId, event.metadata.postId!!))
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