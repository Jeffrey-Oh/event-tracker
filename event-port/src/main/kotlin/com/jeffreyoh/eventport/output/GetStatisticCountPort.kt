package com.jeffreyoh.eventport.output

import com.jeffreyoh.eventcore.domain.event.EventType
import reactor.core.publisher.Mono

interface GetStatisticCountPort {

    fun getCount(componentId: Long, eventType: EventType): Mono<Long>

}