package com.jeffreyoh.eventtracker.port.input

import com.jeffreyoh.eventtracker.core.domain.event.EventCommand
import reactor.core.publisher.Mono

interface SaveEventUseCase {

    fun saveEvent(command: EventCommand.SaveEventCommand): Mono<Void>

}