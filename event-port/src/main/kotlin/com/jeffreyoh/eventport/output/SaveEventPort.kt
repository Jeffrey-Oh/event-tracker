package com.jeffreyoh.eventport.output

import com.jeffreyoh.eventcore.domain.event.EventCommand
import reactor.core.publisher.Mono

interface SaveEventPort {
    fun saveToRedis(command: EventCommand.SaveEventCommand): Mono<Void>
}