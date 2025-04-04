package com.jeffreyoh.eventtracker.application.config

import com.jeffreyoh.eventtracker.application.service.SaveEventService
import com.jeffreyoh.eventtracker.port.input.SaveEventUseCase
import com.jeffreyoh.eventtracker.port.output.EventRedisPort
import com.jeffreyoh.eventtracker.port.output.StatisticsRedisPort
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