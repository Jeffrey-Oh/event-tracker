package com.jeffreyoh.eventapplication.service

import com.jeffreyoh.eventcore.domain.event.Event
import com.jeffreyoh.eventcore.domain.event.EventCommand
import com.jeffreyoh.eventcore.domain.event.EventMetadata
import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.input.SaveEventUseCase
import com.jeffreyoh.eventport.output.IncrementCountPort
import com.jeffreyoh.eventport.output.SaveEventPort
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
class SaveEventServiceTest {

    @MockK private lateinit var saveEventPort: SaveEventPort
    @MockK private lateinit var incrementCountPort: IncrementCountPort
    private lateinit var saveEventService: SaveEventUseCase

    @BeforeEach
    fun setUp() {
        saveEventService = SaveEventService(saveEventPort, incrementCountPort)
    }

    @Test
    fun `이벤트 command 를 전달받아 redis 저장 port가 호출된다`() {
        // given
        val command = EventCommand.SaveEventCommand(
            eventType = EventType.CLICK,
            userId = 1L,
            sessionId = "session-123",
            metadata = EventMetadata(
                componentId = 1000L,
                elementId = "element-123",
                targetUrl = "https://jeffrey-oh.click"
            )
        )

        val slot = slot<Event>()
        val event = command.toEvent()

        every { saveEventPort.saveToRedis(capture(slot)).then(incrementCountPort.incrementCount(event.metadata.componentId, event.eventType)) } returns Mono.empty()

        // when
        val result = saveEventService.saveEvent(command)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        assertThat(slot.captured)
            .usingRecursiveComparison()
            .ignoringFields("createdAt")
            .isEqualTo(event)
    }

}