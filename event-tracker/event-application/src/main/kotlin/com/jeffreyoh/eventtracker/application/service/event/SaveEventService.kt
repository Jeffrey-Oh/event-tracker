package com.jeffreyoh.eventtracker.application.service.event

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.model.event.EventCommand
import com.jeffreyoh.eventtracker.application.port.`in`.SaveEventUseCase
import com.jeffreyoh.eventtracker.application.port.out.EventRedisPort
import com.jeffreyoh.eventtracker.application.port.out.RecentSearchRedisPort
import com.jeffreyoh.eventtracker.application.port.out.StatisticsRedisPort
import io.github.oshai.kotlinlogging.KotlinLogging
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

class SaveEventService(
    private val eventRedisPort: EventRedisPort,
    private val statisticsRedisPort: StatisticsRedisPort,
    private val recentSearchRedisPort: RecentSearchRedisPort
): SaveEventUseCase {

    override fun saveEvent(command: EventCommand.SaveEvent): Mono<Void> {
        log.debug { "UseCase handle called: $command" }
        val event = command.toEvent()

        return eventRedisPort.saveToRedis(event)
            .then(
                when(event.eventType) {
                    EventType.CLICK -> statisticsRedisPort.incrementEventCount(event.eventType, event.metadata)
                    EventType.PAGE_VIEW -> statisticsRedisPort.incrementEventCount(event.eventType, event.metadata)
                    EventType.SEARCH -> {
                        statisticsRedisPort.incrementEventCount(event.eventType, event.metadata)
                            .then(recentSearchRedisPort.saveRecentKeyword(event.userId!!, event.metadata.keyword!!))
                    }
                    EventType.LIKE -> statisticsRedisPort.incrementLike(event.metadata.componentId, event.metadata.postId!!)
                    EventType.UNLIKE -> statisticsRedisPort.decrementLike(event.metadata.componentId, event.metadata.postId!!)
                    else -> Mono.empty()
                }
            )
    }

}