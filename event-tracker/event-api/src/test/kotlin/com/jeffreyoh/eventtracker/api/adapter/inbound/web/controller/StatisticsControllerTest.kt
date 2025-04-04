package com.jeffreyoh.eventtracker.api.adapter.inbound.web.controller

import com.jeffreyoh.eventapi.adapter.inbound.web.dto.EventStatisticsDTO
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.port.input.GetEventStatisticsUseCase
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
        val componentId = 1000L
        val expectedResult = 100L

        given(
            if (eventType == EventType.LIKE)statisticsUseCase.getLikeCount(componentId, 1L) // postId는 임의로 설정
            else statisticsUseCase.getCount(componentId, eventType)
        )
            .willReturn(Mono.just(100L))

        // when
        var uri = "/api/statistics/${eventType}/$componentId"
        if (eventType == EventType.LIKE) uri = "/api/statistics/like/$componentId/1" // postId는 임의로 설정
        val response = webTestClient.get()
            .uri(uri)
            .exchange()

        // then
        response.expectStatus().isOk
            .expectBody(EventStatisticsDTO.EventStatisticsResponse::class.java)
            .consumeWith {
                assertEquals(componentId, it.responseBody!!.componentId)
                assertEquals(expectedResult, it.responseBody!!.count)
            }
    }

    @Test
    fun `존재하지 않는 통계 요청은 count 0으로 응답한다`() {
        // given
        val componentId = 9999L
        val eventType = EventType.CLICK

        given(statisticsUseCase.getCount(componentId, eventType))
            .willReturn(Mono.just(0L))

        // when
        val response = webTestClient.get()
            .uri("/api/statistics/${eventType}/$componentId")
            .exchange()

        // then
        response.expectStatus().isOk
            .expectBody(EventStatisticsDTO.EventStatisticsResponse::class.java)
            .consumeWith {
                assertEquals(componentId, it.responseBody!!.componentId)
                assertEquals(0L, it.responseBody!!.count)
            }
    }

}