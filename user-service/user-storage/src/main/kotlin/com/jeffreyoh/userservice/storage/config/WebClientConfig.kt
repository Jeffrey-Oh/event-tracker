package com.jeffreyoh.userservice.storage.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration

@Configuration
class WebClientConfig {

    @Bean
    fun connectionProvider(): ConnectionProvider =
        ConnectionProvider.builder("shared-pool")
            .maxConnections(1000)
            .pendingAcquireMaxCount(2000)
            .pendingAcquireTimeout(Duration.ofSeconds(10))
            .build()

    @Bean
    fun eventTrackerWebClient(connectionProvider: ConnectionProvider): WebClient {
        val httpClient = HttpClient.create(connectionProvider)
        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl("http://event-tracker:8080")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

}