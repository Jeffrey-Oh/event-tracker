package com.jeffreyoh.eventapi.adapter.inbound.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.jeffreyoh.eventapi.adapter.inbound.web.dto.SaveEventDTO
import com.jeffreyoh.eventapi.adapter.inbound.web.handler.EventHandler
import com.jeffreyoh.eventcore.domain.event.EventType
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(controllers = [EventController::class])
class EventControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var eventHandler: EventHandler

    @Test
    fun `이벤트 저장 요청이 handler 로 전달된다`() {
        // given
        val request = SaveEventDTO.SaveEventRequest(
            eventType = EventType.CLICK.name,
            sessionId = "session-123",
            userId = 1,
            metadata = SaveEventDTO.EventMetadataRequest(
                componentId = 1000,
                elementId = "element-123",
                targetUrl = "https://jeffrey-oh.click"
            )
        )

        val json = objectMapper.writeValueAsString(request)

        given(eventHandler.saveEvent(request)).willReturn(Mono.empty())

        // when
        webTestClient.post()
            .uri("/api/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .exchange()
            .expectStatus().isCreated

        // then
        verify(eventHandler, times(1)).saveEvent(request)
    }

}