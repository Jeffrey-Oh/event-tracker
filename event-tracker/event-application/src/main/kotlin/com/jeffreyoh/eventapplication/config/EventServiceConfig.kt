package com.jeffreyoh.eventapplication.config

import com.jeffreyoh.eventapplication.service.SaveEventService
import com.jeffreyoh.eventport.input.SaveEventUseCase
import com.jeffreyoh.eventport.output.EventRedisPort
import com.jeffreyoh.eventport.output.StatisticsRedisPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventServiceConfig {

    @Bean
    fun saveEventUseCase(
        eventRedisPort: EventRedisPort,
        statisticsRedisPort: StatisticsRedisPort,
    ): SaveEventUseCase
        = SaveEventService(
            eventRedisPort,
            statisticsRedisPort,
        )

}