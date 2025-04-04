package com.jeffreyoh.eventtracker.application.config

import com.jeffreyoh.eventtracker.application.service.GetStatisticsService
import com.jeffreyoh.eventtracker.port.input.GetEventStatisticsUseCase
import com.jeffreyoh.eventtracker.port.output.StatisticsRedisPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StatisticsServiceConfig {

    @Bean
    fun getClickStatisticUseCase(
        statisticsRedisPort: StatisticsRedisPort
    ): GetEventStatisticsUseCase =
        GetStatisticsService(statisticsRedisPort)

}
