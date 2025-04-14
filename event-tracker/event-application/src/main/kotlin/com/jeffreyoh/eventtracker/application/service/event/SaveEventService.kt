package com.jeffreyoh.eventtracker.application.service.event

import com.jeffreyoh.eventtracker.application.model.event.EventCommand
import com.jeffreyoh.eventtracker.application.model.event.EventRedisQuery
import com.jeffreyoh.eventtracker.application.port.`in`.SaveEventUseCase
import com.jeffreyoh.eventtracker.application.port.out.EventRedisPort
import com.jeffreyoh.eventtracker.application.port.out.StatisticsRedisPort
import reactor.core.publisher.Mono

class SaveEventService(
    private val eventRedisPort: EventRedisPort,
    private val statisticsRedisPort: StatisticsRedisPort
): SaveEventUseCase {

    override fun saveEvent(command: EventCommand.SaveEvent): Mono<Void> {
        return eventRedisPort.saveToRedis(command.toEvent())
            .then(statisticsRedisPort.saveEventCount(EventRedisQuery.fromQuery(command)))
    }

}