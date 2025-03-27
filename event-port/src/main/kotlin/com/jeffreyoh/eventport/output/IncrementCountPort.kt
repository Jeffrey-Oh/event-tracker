package com.jeffreyoh.eventport.output

import com.jeffreyoh.eventcore.domain.event.EventType
import reactor.core.publisher.Mono

interface IncrementCountPort {

    fun incrementCount(componentId: Long, eventType: EventType): Mono<Void>

}