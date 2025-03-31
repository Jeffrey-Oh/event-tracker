package com.jeffreyoh.eventapplication.config

import com.jeffreyoh.eventapplication.service.GetStatisticService
import com.jeffreyoh.eventport.input.GetEventStatisticUseCase
import com.jeffreyoh.eventport.output.GetStatisticCountPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class StatisticServiceConfig {

    @Bean
    fun getClickStatisticUseCase(
        getStatisticCountPort: GetStatisticCountPort
    ): GetEventStatisticUseCase =
        GetStatisticService(getStatisticCountPort)

}
