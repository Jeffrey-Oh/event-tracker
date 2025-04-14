package com.jeffreyoh.eventtracker.application.port.`in`

import com.jeffreyoh.eventtracker.application.model.event.EventCommand
import reactor.core.publisher.Mono

interface SaveEventUseCase {

    fun saveEvent(command: EventCommand.SaveEvent): Mono<Void>

}