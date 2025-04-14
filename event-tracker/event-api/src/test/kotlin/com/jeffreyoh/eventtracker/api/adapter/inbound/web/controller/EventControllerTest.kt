package com.jeffreyoh.eventtracker.api.adapter.inbound.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto.SaveEventDTO
import com.jeffreyoh.eventtracker.application.port.`in`.SaveEventUseCase
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
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
    private lateinit var saveEventUseCase: SaveEventUseCase

    @ParameterizedTest
    @EnumSource(EventType::class)
    fun `이벤트 저장 요청이 usecase 로 전달된다`(eventType: EventType) {
        // given
        val request = SaveEventDTO.SaveEventRequest(
            eventType = eventType.name,
            sessionId = "session-123",
            userId = 1,
            metadata = SaveEventDTO.EventMetadataRequest(
                componentId = 1000,
                elementId = "element-123",
                keyword = "keyword-123",
                postId = 1L,
            )
        )

        val command = request.toCommand()

        given(saveEventUseCase.saveEvent(command)).willReturn(Mono.empty())

        // when
        webTestClient.post()
            .uri("/api/events")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus().isCreated

        // then
        verify(saveEventUseCase, times(1)).saveEvent(command)
    }

}