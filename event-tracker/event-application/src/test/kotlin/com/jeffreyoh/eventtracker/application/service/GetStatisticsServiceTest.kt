package com.jeffreyoh.eventtracker.application.service

import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.port.output.StatisticsRedisPort
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
class GetStatisticsServiceTest {

    @MockK private lateinit var statisticsRedisPort: StatisticsRedisPort
    private lateinit var getStatisticsService: GetStatisticsService

    @BeforeEach
    fun setUp() {
        getStatisticsService = GetStatisticsService(statisticsRedisPort)
    }

    @ParameterizedTest
    @EnumSource(EventType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNLIKE"])
    fun `이벤트 통계를 조회한다`(eventType: EventType) {
        // given
        val componentId = 1000L
        val expectedCount = 500L

        val eventTypeSlot = slot<EventType>()
        val eventMetadataSlot = slot<EventMetadata>()

        every {
            when(eventType) {
                EventType.CLICK -> statisticsRedisPort.getEventCount(capture(eventTypeSlot), capture(eventMetadataSlot))
                EventType.PAGE_VIEW -> statisticsRedisPort.getEventCount(capture(eventTypeSlot), capture(eventMetadataSlot))
                EventType.SEARCH -> statisticsRedisPort.getEventCount(capture(eventTypeSlot), capture(eventMetadataSlot))
                EventType.LIKE -> statisticsRedisPort.getEventCount(EventType.LIKE, capture(eventMetadataSlot))
                else -> Mono.empty()
            }
        } returns Mono.just(expectedCount)

        // when
        val result = when(eventType) {
            EventType.LIKE -> getStatisticsService.getLikeCount(componentId, 1L) // postId는 임의로 설정
            else -> getStatisticsService.getCount(componentId, eventType)
        }

        // then
        StepVerifier.create(result)
            .assertNext {
                assertThat(it).isEqualTo(expectedCount)
            }
            .verifyComplete()

        verify(exactly = 1) {
            when(eventType) {
                EventType.CLICK -> statisticsRedisPort.getEventCount(capture(eventTypeSlot), capture(eventMetadataSlot))
                EventType.PAGE_VIEW -> statisticsRedisPort.getEventCount(capture(eventTypeSlot), capture(eventMetadataSlot))
                EventType.SEARCH -> statisticsRedisPort.getEventCount(capture(eventTypeSlot), capture(eventMetadataSlot))
                EventType.LIKE -> statisticsRedisPort.getEventCount(EventType.LIKE, capture(eventMetadataSlot))
                else -> Mono.empty()
            }
        }
    }

}