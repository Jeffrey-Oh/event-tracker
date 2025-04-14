package com.jeffreyoh.eventtracker.application.config

import com.jeffreyoh.eventtracker.application.port.`in`.GetEventStatisticsUseCase
import com.jeffreyoh.eventtracker.application.port.out.StatisticsRedisPort
import com.jeffreyoh.eventtracker.application.service.statistics.GetStatisticsService
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
