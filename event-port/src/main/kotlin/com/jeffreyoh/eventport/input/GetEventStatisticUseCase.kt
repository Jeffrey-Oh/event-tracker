package com.jeffreyoh.eventport.input

import com.jeffreyoh.eventcore.domain.event.EventType
import reactor.core.publisher.Mono

interface GetEventStatisticUseCase {

    fun getCount(componentId: Long, eventType: EventType): Mono<Long>

}