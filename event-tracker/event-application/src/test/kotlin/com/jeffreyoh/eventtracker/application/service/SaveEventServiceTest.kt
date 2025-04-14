package com.jeffreyoh.eventtracker.application.service

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.model.event.EventCommand
import com.jeffreyoh.eventtracker.application.model.event.EventRedisQuery
import com.jeffreyoh.eventtracker.application.port.`in`.SaveEventUseCase
import com.jeffreyoh.eventtracker.application.port.out.EventRedisPort
import com.jeffreyoh.eventtracker.application.port.out.StatisticsRedisPort
import com.jeffreyoh.eventtracker.application.service.event.SaveEventService
import com.jeffreyoh.eventtracker.core.domain.event.Event
import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
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
class SaveEventServiceTest {

    @MockK private lateinit var eventRedisPort: EventRedisPort
    @MockK private lateinit var statisticsRedisPort: StatisticsRedisPort
    private lateinit var saveEventService: SaveEventUseCase

    @BeforeEach
    fun setUp() {
        saveEventService = SaveEventService(
            eventRedisPort,
            statisticsRedisPort
        )
    }

    @ParameterizedTest
    @EnumSource(EventType::class, mode = EnumSource.Mode.EXCLUDE, names = ["LIKE", "UNLIKE"])
    fun `이벤트 command 를 전달받아 redis 저장 port가 호출된다`(eventType: EventType) {
        // given
        val command = EventCommand.SaveEvent(
            eventType = eventType,
            userId = 1L,
            sessionId = "session-123",
            metadata = EventMetadata(
                componentId = 1000L,
                elementId = "element-123",
                postId = 1L,
                keyword = "keyword"
            )
        )

        val eventSlot = slot<Event>()

        val event = command.toEvent()

        every {
            eventRedisPort.saveToRedis(capture(eventSlot))
                .then(statisticsRedisPort.saveEventCount(EventRedisQuery.toQuery(command)))
        } returns Mono.empty()

        // when
        val result = saveEventService.saveEvent(command)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        assertThat(eventSlot.captured)
            .usingRecursiveComparison()
            .ignoringFields("createdAt")
            .isEqualTo(event)

        verify(exactly = 1) {
            eventRedisPort.saveToRedis(capture(eventSlot))
                .then(statisticsRedisPort.saveEventCount(EventRedisQuery.toQuery(command)))
        }

    }

}