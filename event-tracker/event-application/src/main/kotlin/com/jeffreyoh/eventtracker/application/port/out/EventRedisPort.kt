package com.jeffreyoh.eventtracker.application.port.out

import com.jeffreyoh.eventtracker.core.domain.event.Event
import reactor.core.publisher.Mono

interface EventRedisPort {

    fun saveToRedis(event: Event): Mono<Void>

}