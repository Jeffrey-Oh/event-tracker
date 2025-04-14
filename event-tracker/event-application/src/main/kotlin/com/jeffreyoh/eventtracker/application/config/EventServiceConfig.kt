package com.jeffreyoh.eventtracker.application.config

import com.jeffreyoh.eventtracker.application.port.`in`.SaveEventUseCase
import com.jeffreyoh.eventtracker.application.port.out.EventRedisPort
import com.jeffreyoh.eventtracker.application.port.out.RecentSearchRedisPort
import com.jeffreyoh.eventtracker.application.port.out.StatisticsRedisPort
import com.jeffreyoh.eventtracker.application.service.event.SaveEventService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventServiceConfig {

    @Bean
    fun saveEventUseCase(
        eventRedisPort: EventRedisPort,
        statisticsRedisPort: StatisticsRedisPort,
        recentSearchRedisPort: RecentSearchRedisPort
    ): SaveEventUseCase
        = SaveEventService(
            eventRedisPort,
            statisticsRedisPort,
            recentSearchRedisPort
        )

}