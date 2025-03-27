package com.jeffreyoh.eventapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.jeffreyoh.eventapi.adapter.inbound.web.controller.EventController
import com.jeffreyoh.eventapi.adapter.inbound.web.dto.SaveEventDTO
import com.jeffreyoh.eventapi.adapter.inbound.web.handler.EventHandler
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(controllers = [EventController::class])
class EventControllerValidationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var eventHandler: EventHandler

    @Test
    fun `eventType 이 공백이면 400`() {
        val request = SaveEventDTO.SaveEventRequest(
            eventType = "", // ❌
            sessionId = "session-123",
            userId = 1L,
            metadata = SaveEventDTO.EventMetadataRequest(1000L, "element-123", "https://jeffrey-oh.click")
        )

        postAndExpectBadRequest(request)
    }

    @Test
    fun `sessionId 가 공백이면 400`() {
        val request = SaveEventDTO.SaveEventRequest(
            eventType = "CLICK",
            sessionId = " ", // ❌
            userId = 1L,
            metadata = SaveEventDTO.EventMetadataRequest(1000L, "element-123", "https://jeffrey-oh.click")
        )

        postAndExpectBadRequest(request)
    }

    @Test
    fun `metadata-elementId 가 빈 문자열이면 400`() {
        val request = SaveEventDTO.SaveEventRequest(
            eventType = "CLICK",
            sessionId = "session-123",
            userId = 1L,
            metadata = SaveEventDTO.EventMetadataRequest(
                componentId = 1000L,
                elementId = "", // ❌
                targetUrl = "https://jeffrey-oh.click"
            )
        )

        postAndExpectBadRequest(request)
    }

    private fun postAndExpectBadRequest(request: SaveEventDTO.SaveEventRequest) {
        val json = objectMapper.writeValueAsString(request)

        webTestClient.post()
            .uri("/api/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .exchange()
            .expectStatus().isBadRequest
    }

}