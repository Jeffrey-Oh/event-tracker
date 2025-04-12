package com.jeffreyoh.userservice.storage.adapter.outbound.eventtracker

import com.jeffreyoh.userservice.core.command.EventTrackerCommand
import com.jeffreyoh.userservice.port.out.EventTrackerPort
import com.jeffreyoh.userservice.storage.adapter.outbound.eventtracker.dto.EventTrackerRequestDTO
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

    override fun sendEvent(command: EventTrackerCommand.SaveEventCommand): Mono<Void> {
        EventTrackerRequestDTO.SaveEventRequest(
            eventType = command.eventType,
            userId = command.userId,
            sessionId = command.sessionId,
            metadata = command.metadata
        ).let { request ->
            return webClient.post()
                .uri("$eventTrackerUrl/api/events")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void::class.java)
        }
    }

}