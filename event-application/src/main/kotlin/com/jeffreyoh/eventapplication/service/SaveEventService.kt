package com.jeffreyoh.eventapplication.service

import com.jeffreyoh.eventcore.domain.event.EventCommand
import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventcore.domain.event.toJson
import com.jeffreyoh.eventport.input.SaveEventUseCase
import com.jeffreyoh.eventport.output.DecrementCountPort
import com.jeffreyoh.eventport.output.DeleteEventPort
import com.jeffreyoh.eventport.output.IncrementCountPort
import com.jeffreyoh.eventport.output.ReadEventPort
import com.jeffreyoh.eventport.output.SaveEventPort
import io.github.oshai.kotlinlogging.KotlinLogging
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

class SaveEventService(
    private val saveEventPort: SaveEventPort,
    private val deleteEventPort: DeleteEventPort,
    private val readEventPort: ReadEventPort,
    private val incrementCountPort: IncrementCountPort,
    private val decrementCountPort: DecrementCountPort
): SaveEventUseCase {

    override fun saveEvent(command: EventCommand.SaveEventCommand): Mono<Void> {
        log.debug { "UseCase handle called: $command" }
        val event = command.toEvent()

        return if (event.eventType == EventType.LIKE) {
            val key = "events:${event.eventType.name.lowercase()}:user:${event.userId!!}:post:${event.metadata.postId}"

            readEventPort.readLikeFromRedisKey(key)
                .defaultIfEmpty("MISSING")
                .flatMap { cachedEvent ->
                    if (cachedEvent == "MISSING") {
                        log.info { "Event does not exist in Redis, saving it" }
                        saveEventPort.saveLikeEventToRedis(key, event)
                            .then(incrementCountPort.incrementLikeCount(event.metadata.componentId, event.metadata.postId!!))
                    } else {
                        log.info { "Event already exists in Redis, deleting it" }
                        deleteEventPort.deleteFromRedisKey(key)
                            .then(decrementCountPort.decrementLikeCount(event.metadata.componentId, event.metadata.postId!!))
                    }
                }
        } else {
            saveEventPort.saveToRedis(event)
                .then(incrementCountPort.incrementCount(event.metadata.componentId, event.eventType))
        }
    }

}