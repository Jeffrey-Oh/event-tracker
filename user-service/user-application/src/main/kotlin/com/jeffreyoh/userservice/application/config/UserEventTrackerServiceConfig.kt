package com.jeffreyoh.userservice.application.config

import com.jeffreyoh.userservice.application.service.UserEventTrackerService
import com.jeffreyoh.userservice.application.port.`in`.EventTrackerUseCase
import com.jeffreyoh.userservice.application.port.out.EventTrackerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserEventTrackerServiceConfig {

    @Bean
    fun eventTrackerUseCase(
        eventTrackerPort: EventTrackerPort
    ) : EventTrackerUseCase =
        UserEventTrackerService(eventTrackerPort)

}