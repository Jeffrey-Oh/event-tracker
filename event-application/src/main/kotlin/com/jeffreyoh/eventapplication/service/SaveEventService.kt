package com.jeffreyoh.eventapplication.service

import com.jeffreyoh.eventcore.domain.event.EventCommand
import com.jeffreyoh.eventport.input.SaveEventUseCase
import com.jeffreyoh.eventport.output.IncrementCountPort
import com.jeffreyoh.eventport.output.SaveEventPort
import io.github.oshai.kotlinlogging.KotlinLogging
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

class SaveEventService(
    private val saveEventPort: SaveEventPort,
    private val incrementCountPort: IncrementCountPort
): SaveEventUseCase {

    override fun saveEvent(command: EventCommand.SaveEventCommand): Mono<Void> {
        log.debug { "UseCase handle called: $command" }
        val event = command.toEvent()

        return saveEventPort.saveToRedis(event)
            .then(
                incrementCountPort.incrementCount(event.metadata.componentId, event.eventType)
            )
    }

}