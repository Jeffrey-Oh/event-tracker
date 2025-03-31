package com.jeffreyoh.eventport.input

import com.jeffreyoh.eventcore.domain.event.EventCommand
import reactor.core.publisher.Mono

interface SaveEventUseCase {

    fun saveEvent(command: EventCommand.SaveEventCommand): Mono<Void>

}