package com.jeffreyoh.userservice.storage.adapter.outbound.eventtracker

import com.jeffreyoh.userservice.application.model.event.EventTrackerRequest
import com.jeffreyoh.userservice.application.port.out.EventTrackerPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.time.Duration

private val log = KotlinLogging.logger {}

@Component
class EventTrackerAdapter(
    private val webClient: WebClient,
    @Value("\${event.tracker.url}") private val eventTrackerUrl: String
) : EventTrackerPort {

    override fun sendEvent(request: EventTrackerRequest.SaveEvent): Mono<Void> {
        val startTime = System.nanoTime()

        return webClient.post()
            .uri("$eventTrackerUrl/api/events")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Void::class.java)
            .timeout(Duration.ofMillis(500)) // 타임아웃 500ms
            .doOnSuccess {
                val durationMs = (System.nanoTime() - startTime) / 1_000_000
                log.debug { "이벤트 전송 성공: eventType=${request.eventType}, userId=${request.userId}, time=${durationMs}ms" }
            }
            .doOnError { e ->
                val durationMs = (System.nanoTime() - startTime) / 1_000_000
                val status = if (e is WebClientResponseException) "status=${e.statusCode}" else "unknown"
                log.warn { "이벤트 전송 실패: eventType=${request.eventType}, userId=${request.userId}, $status, error=${e.message}, time=${durationMs}ms" }
            }
            .onErrorResume { Mono.empty() } // 실패 시 무시, 검색 응답 우선
    }

}