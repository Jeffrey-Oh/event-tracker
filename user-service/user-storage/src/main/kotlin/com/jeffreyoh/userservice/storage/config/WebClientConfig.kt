package com.jeffreyoh.userservice.storage.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun eventTrackerWebClient(): WebClient =
        WebClient.builder()
            .baseUrl("http://localhost:8080") // event-tracker API
            .build()

}