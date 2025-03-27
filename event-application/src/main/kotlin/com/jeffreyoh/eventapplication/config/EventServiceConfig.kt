package com.jeffreyoh.eventapplication.config

import com.jeffreyoh.eventapplication.service.SaveEventService
import com.jeffreyoh.eventport.input.SaveEventUseCase
import com.jeffreyoh.eventport.output.IncrementCountPort
import com.jeffreyoh.eventport.output.SaveEventPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventServiceConfig {

    @Bean
    fun saveEventUseCase(
        saveEventPort: SaveEventPort,
        incrementCountPort: IncrementCountPort
    ): SaveEventUseCase
        = SaveEventService(saveEventPort, incrementCountPort)

}