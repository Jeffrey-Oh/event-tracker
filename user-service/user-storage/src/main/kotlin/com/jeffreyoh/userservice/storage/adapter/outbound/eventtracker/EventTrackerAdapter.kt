package com.jeffreyoh.userservice.storage.adapter.outbound.eventtracker

import com.jeffreyoh.userservice.application.model.event.EventTrackerRequest
import com.jeffreyoh.userservice.application.port.out.EventTrackerPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

private val log = KotlinLogging.logger {}

@Component
class EventTrackerAdapter(
    private val webClient: WebClient,
    @Value("\${event.tracker.url}") private val eventTrackerUrl: String,
    @Value("\${event.tracker.timeout:2000}") private val timeoutMs: Long = 2000
) : EventTrackerPort {

    override fun sendEvent(request: EventTrackerRequest.SaveEvent): Mono<Void> {
        val startTime = System.nanoTime()

        return webClient.post()
            .uri("$eventTrackerUrl/api/events")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Void::class.java)
            .timeout(Duration.ofMillis(timeoutMs))
            .retryWhen(
                Retry.backoff(3, Duration.ofMillis(200))
                    .jitter(0.1)
                    .filter { it is WebClientResponseException || it is java.util.concurrent.TimeoutException }
                    .doBeforeRetry { signal ->
                        log.debug { "🔄 이벤트 전송 재시도: eventType=${request.eventType}, userId=${request.userId}, attempt=${signal.totalRetries() + 1}, error=${signal.failure().message}" }
                    }
            )
            .doOnSuccess {
                val durationMs = (System.nanoTime() - startTime) / 1_000_000
                log.debug { "📡 이벤트 전송 성공: eventType=${request.eventType}, userId=${request.userId}, sessionId=${request.sessionId}, time=${durationMs}ms" }
            }
            .doOnError { e ->
                val durationMs = (System.nanoTime() - startTime) / 1_000_000
                val status = if (e is WebClientResponseException) "status=${e.statusCode}" else "unknown"
                log.warn(e) { "⚠️ 이벤트 전송 실패: eventType=${request.eventType}, userId=${request.userId}, sessionId=${request.sessionId}, $status, error=${e.message}, time=${durationMs}ms" }
            }
            .onErrorResume { Mono.empty() } // 실패 시 무시, 검색 응답 우선
    }

}