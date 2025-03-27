package com.jeffreyoh.eventapi.adapter.inbound.web.controller

import com.jeffreyoh.eventapi.adapter.inbound.web.dto.ClickStatisticDTO
import com.jeffreyoh.eventapi.adapter.inbound.web.handler.StatisticHandler
import com.jeffreyoh.eventcore.domain.event.EventType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(StatisticController::class)
class StatisticControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockitoBean
    private lateinit var statisticHandler: StatisticHandler

    @Test
    fun `정상적인 통계 조회 요청은 200 OK와 통계 결과를 반환한다`() {
        // given
        val componentId = 1000L
        val eventType = EventType.CLICK
        val expectedResult = 100L

        given(statisticHandler.getClickCount(componentId, eventType))
            .willReturn(Mono.just(ClickStatisticDTO.ClistStatisticResponse(componentId, expectedResult)))

        // when
        val response = webTestClient.get()
            .uri("/api/statistics/${eventType}/$componentId")
            .exchange()

        // then
        response.expectStatus().isOk
            .expectBody(ClickStatisticDTO.ClistStatisticResponse::class.java)
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

        given(statisticHandler.getClickCount(componentId, eventType))
            .willReturn(Mono.just(ClickStatisticDTO.ClistStatisticResponse(componentId, 0L)))

        // when
        val response = webTestClient.get()
            .uri("/api/statistics/${eventType}/$componentId")
            .exchange()

        // then
        response.expectStatus().isOk
            .expectBody(ClickStatisticDTO.ClistStatisticResponse::class.java)
            .consumeWith {
                assertEquals(componentId, it.responseBody!!.componentId)
                assertEquals(0L, it.responseBody!!.count)
            }
    }

}