package com.jeffreyoh.eventtracker.application.service

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.model.statistics.StatisticsCommand
import com.jeffreyoh.eventtracker.application.model.statistics.GetStatisticsRedisQuery
import com.jeffreyoh.eventtracker.application.port.out.StatisticsRedisPort
import com.jeffreyoh.eventtracker.application.service.statistics.GetStatisticsService
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
        val expectedCount = 500L

        val querySlot = slot<GetStatisticsRedisQuery>()

        every { statisticsRedisPort.getEventCount(capture(querySlot)) } returns Mono.just(expectedCount)

        // when
        val result = getStatisticsService.getCount(
            StatisticsCommand.GetStatistics(
                eventType = eventType,
                componentId = eventType.componentId,
                keyword = null,
                postId = null,
            )
        )

        // then
        StepVerifier.create(result)
            .assertNext {
                assertThat(it).isEqualTo(expectedCount)
            }
            .verifyComplete()

        verify(exactly = 1) { statisticsRedisPort.getEventCount(querySlot.captured) }
    }

}