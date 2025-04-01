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

    override fun saveEvent(command: EventCommand.SaveEventCommand): Mono<Void> {
        log.debug { "UseCase handle called: $command" }
        val event = command.toEvent()

        return if (event.eventType == EventType.LIKE) {
            val key = "events:${event.eventType.name.lowercase()}:user:${event.userId!!}:post:${event.metadata.postId}"

            eventRedisPort.readLikeFromRedisKey(key)
                .defaultIfEmpty("MISSING")
                .flatMap { cachedEvent ->
                    if (cachedEvent == "MISSING") {
                        log.info { "Event does not exist in Redis, saving it" }
                        eventRedisPort.saveLikeEventToRedis(key, event)
                            .then(statisticsRedisPort.incrementLikeCount(event.metadata.componentId, event.metadata.postId!!))
                    } else {
                        log.info { "Event already exists in Redis, deleting it" }
                        eventRedisPort.deleteFromRedisKey(key)
                            .then(statisticsRedisPort.decrementLikeCount(event.metadata.componentId, event.metadata.postId!!))
                    }
                }
        } else {
            eventRedisPort.saveToRedis(event)
                .then(statisticsRedisPort.incrementCount(event.metadata.componentId, event.eventType))
        }
    }

}