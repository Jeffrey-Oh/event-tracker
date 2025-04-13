package com.jeffreyoh.eventtracker.application.service

import com.jeffreyoh.eventtracker.core.domain.event.EventCommand
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.port.input.SaveEventUseCase
import com.jeffreyoh.eventtracker.port.output.EventRedisPort
import com.jeffreyoh.eventtracker.port.output.RecentSearchRedisPort
import com.jeffreyoh.eventtracker.port.output.StatisticsRedisPort
import io.github.oshai.kotlinlogging.KotlinLogging
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

class SaveEventService(
    private val eventRedisPort: EventRedisPort,
    private val statisticsRedisPort: StatisticsRedisPort,
    private val recentSearchRedisPort: RecentSearchRedisPort
): SaveEventUseCase {

    override fun saveEvent(command: EventCommand.SaveEventCommand): Mono<Void> {
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