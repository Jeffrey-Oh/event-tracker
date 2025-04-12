package com.jeffreyoh.eventtracker.application.service

import com.jeffreyoh.eventtracker.core.domain.event.Event
import com.jeffreyoh.eventtracker.core.domain.event.EventCommand
import com.jeffreyoh.eventtracker.core.domain.event.EventType
import com.jeffreyoh.eventtracker.port.input.SaveEventUseCase
import com.jeffreyoh.eventtracker.port.output.EventRedisPort
import com.jeffreyoh.eventtracker.port.output.RecentSearchRedisPort
import com.jeffreyoh.eventtracker.port.output.StatisticsRedisPort
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
class SaveEventServiceTest {

    @MockK private lateinit var eventRedisPort: EventRedisPort
    @MockK private lateinit var statisticsRedisPort: StatisticsRedisPort
    @MockK private lateinit var recentSearchRedisPort: RecentSearchRedisPort
    private lateinit var saveEventService: SaveEventUseCase

    @BeforeEach
    fun setUp() {
        saveEventService = SaveEventService(
            eventRedisPort,
            statisticsRedisPort,
            recentSearchRedisPort
        )
    }

    @ParameterizedTest
    @EnumSource(EventType::class, mode = EnumSource.Mode.EXCLUDE, names = ["LIKE", "UNLIKE"])
    fun `이벤트 command 를 전달받아 redis 저장 port가 호출된다`(eventType: EventType) {
        // given
        val command = EventCommand.SaveEventCommand(
            eventType = eventType,
            userId = 1L,
            sessionId = "session-123",
            metadata = EventCommand.EventMetadata(
                componentId = 1000L,
                elementId = "element-123",
                postId = 1L,
                keyword = "keyword"
            )
        )

        val eventSlot = slot<Event>()
        val eventTypeSlot = slot<EventType>()
        val eventMetadataSlot = slot<EventCommand.EventMetadata>()
        val userIdSlot = slot<Long>()
        val keywordSlot = slot<String>()

        val event = command.toEvent()

        every {
            eventRedisPort.saveToRedis(capture(eventSlot))
                .then(
                    when (eventType) {
                        EventType.CLICK -> statisticsRedisPort.incrementEventCount(
                            capture(eventTypeSlot),
                            capture(eventMetadataSlot)
                        )

                        EventType.PAGE_VIEW -> statisticsRedisPort.incrementEventCount(
                            capture(eventTypeSlot),
                            capture(eventMetadataSlot)
                        )

                        EventType.SEARCH -> statisticsRedisPort.incrementEventCount(
                            capture(eventTypeSlot),
                            capture(eventMetadataSlot)
                        ).then(recentSearchRedisPort.saveRecentKeyword(capture(userIdSlot), capture(keywordSlot)))

                        else -> Mono.empty()
                    }
                )
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
                .then(
                    when (eventType) {
                        EventType.CLICK -> statisticsRedisPort.incrementEventCount(
                            capture(eventTypeSlot),
                            capture(eventMetadataSlot)
                        )

                        EventType.PAGE_VIEW -> statisticsRedisPort.incrementEventCount(
                            capture(eventTypeSlot),
                            capture(eventMetadataSlot)
                        )

                        EventType.SEARCH -> statisticsRedisPort.incrementEventCount(
                            capture(eventTypeSlot),
                            capture(eventMetadataSlot)
                        ).then(recentSearchRedisPort.saveRecentKeyword(capture(userIdSlot), capture(keywordSlot)))

                        else -> Mono.empty()
                    }
                )
        }

    }

    @Test
    fun `LIKE 이벤트 - 캐시에 없으면 저장한다`() {
        // given
        val command = EventCommand.SaveEventCommand(
            eventType = EventType.LIKE,
            userId = 1L,
            sessionId = "session-123",
            metadata = EventCommand.EventMetadata(
                componentId = 1000L,
                elementId = "element-123",
                postId = 1L,
                keyword = "keyword"
            )
        )

        val eventSlot = slot<Event>()
        val keySlot = slot<String>()
        val componentIdSlot = slot<Long>()
        val postIdSlot = slot<Long>()

        val event = command.toEvent()

        every { eventRedisPort.readLikeFromRedisKey(capture(keySlot)) } returns Mono.empty()
        every { eventRedisPort.saveLikeEventToRedis(capture(keySlot), capture(eventSlot)) } returns Mono.empty()
        every { statisticsRedisPort.incrementLike(capture(componentIdSlot), capture(postIdSlot)) } returns Mono.empty()

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
            eventRedisPort.readLikeFromRedisKey(capture(keySlot))
            eventRedisPort.saveLikeEventToRedis(capture(keySlot), capture(eventSlot))
            statisticsRedisPort.incrementLike(capture(componentIdSlot), capture(postIdSlot))
        }
    }

    @Test
    fun `LIKE 이벤트 - 캐시에 이미 있으면 삭제한다`() {
        // given
        val command = EventCommand.SaveEventCommand(
            eventType = EventType.LIKE,
            userId = 1L,
            sessionId = "session-123",
            metadata = EventCommand.EventMetadata(
                componentId = 1000L,
                elementId = "element-123",
                postId = 1L,
                keyword = "keyword"
            )
        )

        val keySlot = slot<String>()
        val componentIdSlot = slot<Long>()
        val postIdSlot = slot<Long>()

        every { eventRedisPort.readLikeFromRedisKey(capture(keySlot)) } returns Mono.just("cached")
        every { eventRedisPort.deleteFromRedisKey(capture(keySlot)) } returns Mono.empty()
        every { statisticsRedisPort.decrementLike(capture(componentIdSlot), capture(postIdSlot)) } returns Mono.empty()

        // when
        val result = saveEventService.saveEvent(command)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        verify(exactly = 1) {
            eventRedisPort.readLikeFromRedisKey(capture(keySlot))
            eventRedisPort.deleteFromRedisKey(capture(keySlot))
            statisticsRedisPort.decrementLike(capture(componentIdSlot), capture(postIdSlot))
        }
    }

}