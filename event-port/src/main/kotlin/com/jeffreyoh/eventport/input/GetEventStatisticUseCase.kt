package com.jeffreyoh.eventport.input

import com.jeffreyoh.eventcore.domain.event.EventType
import reactor.core.publisher.Mono

interface GetClickStatisticUseCase {

    fun getCount(componentId: Long, eventType: EventType): Mono<Long>

}