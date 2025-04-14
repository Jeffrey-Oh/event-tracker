package com.jeffreyoh.eventtracker.application.service.statistics

import com.jeffreyoh.eventtracker.application.model.statistics.StatisticsCommand
import com.jeffreyoh.eventtracker.application.model.statistics.GetStatisticsRedisQuery
import com.jeffreyoh.eventtracker.application.port.`in`.GetEventStatisticsUseCase
import com.jeffreyoh.eventtracker.application.port.out.StatisticsRedisPort
import reactor.core.publisher.Mono

class GetStatisticsService(
    private val statisticsRedisPort: StatisticsRedisPort
): GetEventStatisticsUseCase {

    override fun getCount(command: StatisticsCommand.GetStatistics): Mono<Long> {
        return statisticsRedisPort.getEventCount(GetStatisticsRedisQuery.toQuery(command))
    }

}