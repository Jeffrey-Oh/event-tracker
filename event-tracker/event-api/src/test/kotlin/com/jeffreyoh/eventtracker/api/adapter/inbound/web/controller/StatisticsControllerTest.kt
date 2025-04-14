package com.jeffreyoh.eventtracker.api.adapter.inbound.web.controller

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto.EventStatisticsDTO
import com.jeffreyoh.eventtracker.application.model.statistics.StatisticsCommand
import com.jeffreyoh.eventtracker.application.port.`in`.GetEventStatisticsUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(StatisticsController::class)
class StatisticsControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoBean
    private lateinit var statisticsUseCase: GetEventStatisticsUseCase

    @ParameterizedTest
    @EnumSource(EventType::class)
    fun `정상적인 통계 조회 요청은 200 OK와 통계 결과를 반환한다`(eventType: EventType) {
        // given
        val expectedResult = 100L
        val command = StatisticsCommand.GetStatistics(
            eventType = eventType,
            componentId = eventType.componentId,
            keyword = "keyword",
            postId = 1L,
        )

        given(statisticsUseCase.getCount(command)).willReturn(Mono.just(expectedResult))

        // when
        val response = webTestClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/statistics")
                    .queryParam("eventType", eventType.name)
                    .queryParam("componentId", eventType.componentId)
                    .queryParam("keyword", "keyword")
                    .queryParam("postId", 1L)
                    .build()
            }
            .exchange()

        // then
        response.expectStatus().isOk
            .expectBody(EventStatisticsDTO.EventStatisticsResponse::class.java)
            .consumeWith { assertEquals(expectedResult, it.responseBody!!.count) }
    }

    @Test
    fun `존재하지 않는 통계 요청은 count 0으로 응답한다`() {
        // given
        val eventType = EventType.CLICK
        val componentId = 9999L
        val command = StatisticsCommand.GetStatistics(
            eventType = eventType,
            componentId = eventType.componentId,
            keyword = "keyword",
            postId = 1L,
        )

        given(statisticsUseCase.getCount(command)).willReturn(Mono.just(0L))

        // when
        val response = webTestClient.get()
            .uri { uriBuilder ->
                uriBuilder
                    .path("/api/statistics")
                    .queryParam("eventType", eventType.name)
                    .queryParam("componentId", eventType.componentId)
                    .queryParam("keyword", "keyword")
                    .queryParam("postId", 1L)
                    .build()
            }
            .exchange()

        // then
        response.expectStatus().isOk
            .expectBody(EventStatisticsDTO.EventStatisticsResponse::class.java)
            .consumeWith { assertEquals(0L, it.responseBody!!.count) }
    }

}