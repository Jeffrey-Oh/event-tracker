package com.jeffreyoh.eventtracker.application.port.`in`

import com.jeffreyoh.eventtracker.application.model.statistics.StatisticsCommand
import reactor.core.publisher.Mono

interface GetEventStatisticsUseCase {

    fun getCount(command: StatisticsCommand.GetStatistics): Mono<Long>

}