package com.jeffreyoh.eventapplication.service

import com.jeffreyoh.eventcore.domain.event.EventCommand
import com.jeffreyoh.eventport.input.SaveEventUseCase
import com.jeffreyoh.eventport.output.SaveEventPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Service
class SaveEventService(
    private val saveEventPort: SaveEventPort,
): SaveEventUseCase {

    override fun saveEvent(command: EventCommand.SaveEventCommand): Mono<Void> {
        log.debug { "UseCase handle called: $command" }
        val event = command.toEvent()
        return saveEventPort.saveToRedis(event)
    }

}