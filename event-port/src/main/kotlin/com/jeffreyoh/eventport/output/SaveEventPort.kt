package com.jeffreyoh.eventport.output

import com.jeffreyoh.eventcore.domain.event.Event
import com.jeffreyoh.eventcore.domain.event.EventCommand
import reactor.core.publisher.Mono

interface SaveEventPort {
    fun saveToRedis(event: Event): Mono<Void>
}