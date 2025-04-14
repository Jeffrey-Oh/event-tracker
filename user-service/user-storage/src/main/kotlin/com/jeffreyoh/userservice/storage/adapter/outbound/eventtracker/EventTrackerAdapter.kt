package com.jeffreyoh.userservice.storage.adapter.outbound.eventtracker

import com.jeffreyoh.userservice.application.model.event.EventTrackerRequest
import com.jeffreyoh.userservice.application.port.out.EventTrackerPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class EventTrackerAdapter(
    private val webClient: WebClient
): EventTrackerPort {

    @Value("\${event.tracker.url}")
    private lateinit var eventTrackerUrl: String

    override fun sendEvent(request: EventTrackerRequest.SaveEvent): Mono<Void> {
        return webClient.post()
            .uri("$eventTrackerUrl/api/events")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Void::class.java)
    }

}