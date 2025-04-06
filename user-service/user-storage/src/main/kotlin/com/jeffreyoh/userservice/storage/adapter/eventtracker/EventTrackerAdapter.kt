package com.jeffreyoh.userservice.storage.adapter.eventtracker

import com.jeffreyoh.userservice.core.domain.EventTrackerCommand
import com.jeffreyoh.userservice.port.out.EventTrackerPort
import com.jeffreyoh.userservice.storage.adapter.eventtracker.dto.EventTrackerOutboundDTO
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class EventTrackerAdapter(
    private val webClient: WebClient
): EventTrackerPort {

    override fun sendEvent(command: EventTrackerCommand.PayloadCommand): Mono<Void> {
        EventTrackerOutboundDTO.SaveEventRequest(
            eventType = command.eventType,
            userId = command.userId,
            sessionId = UUID.randomUUID().toString(),
            metadata = EventTrackerOutboundDTO.EventMetadata(
                componentId = 1000L,
                elementId = "elementId-123",
                postId = command.postId
            )
        ).let { request ->
            return webClient.post()
                .uri("/api/events")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void::class.java)
        }
    }

}