package com.jeffreyoh.eventapplication.config

import com.jeffreyoh.eventapplication.service.GetStatisticsService
import com.jeffreyoh.eventport.input.GetEventStatisticsUseCase
import com.jeffreyoh.eventport.output.StatisticsRedisPort
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
