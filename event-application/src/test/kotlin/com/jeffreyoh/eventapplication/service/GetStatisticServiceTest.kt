package com.jeffreyoh.eventapplication.service

import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.output.GetStatisticCountPort
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
class GetStatisticServiceTest {

    @MockK private lateinit var getStatisticCountPort: GetStatisticCountPort
    private lateinit var getStatisticService: GetStatisticService

    @BeforeEach
    fun setUp() {
        getStatisticService = GetStatisticService(getStatisticCountPort)
    }

    @ParameterizedTest
    @EnumSource(EventType::class)
    fun `이벤트 통계를 조회한다`(eventType: EventType) {
        // given
        val componentId = 1000L
        val expectedCount = 500L

        every { getStatisticCountPort.getCount(componentId, eventType) } returns Mono.just(expectedCount)

        // when
        val result = getStatisticService.getCount(componentId, eventType)

        // then
        StepVerifier.create(result)
            .assertNext {
                assertThat(it).isEqualTo(expectedCount)
            }
            .verifyComplete()

        verify(exactly = 1) { getStatisticCountPort.getCount(componentId, eventType) }
    }

}