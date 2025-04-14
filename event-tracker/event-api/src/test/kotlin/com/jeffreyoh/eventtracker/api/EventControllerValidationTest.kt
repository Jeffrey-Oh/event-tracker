package com.jeffreyoh.eventtracker.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.jeffreyoh.eventtracker.api.adapter.inbound.web.controller.EventController
import com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto.SaveEventDTO
import com.jeffreyoh.eventtracker.application.port.`in`.SaveEventUseCase
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
    private lateinit var saveEventUseCase: SaveEventUseCase

    @Test
    fun `eventType 이 공백이면 400`() {
        val request = SaveEventDTO.SaveEventRequest(
            eventType = "", // ❌
            userId = 1L,
            sessionId = "session-123",
            metadata = SaveEventDTO.EventMetadataRequest(1000L, "element-123", "keyword-123", 1L)
        )

        postAndExpectBadRequest(request)
    }

    @Test
    fun `sessionId 가 공백이면 400`() {
        val request = SaveEventDTO.SaveEventRequest(
            eventType = "CLICK",
            userId = 1L,
            sessionId = " ", // ❌
            metadata = SaveEventDTO.EventMetadataRequest(1000L, "element-123", "keyword-123", 1L)
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
                keyword = "keyword-123",
                postId = 1L
            )
        )

        postAndExpectBadRequest(request)
    }

    private fun postAndExpectBadRequest(request: SaveEventDTO.SaveEventRequest) {
        webTestClient.post()
            .uri("/api/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
    }

}