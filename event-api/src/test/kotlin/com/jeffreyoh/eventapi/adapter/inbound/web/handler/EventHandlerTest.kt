package com.jeffreyoh.eventapi.adapter.inbound.web.handler

import com.jeffreyoh.eventapi.adapter.inbound.web.dto.SaveEventDTO
import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.input.SaveEventUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
class EventHandlerTest {

    @MockK private lateinit var saveEventUseCase: SaveEventUseCase
    private lateinit var eventHandler: EventHandler

    @BeforeEach
    fun setUp() {
        eventHandler = EventHandler(saveEventUseCase)
    }

    @Test
    fun `이벤트 저장 요청을 usecase 로 전달한다`() {
        // given
        val request = SaveEventDTO.SaveEventRequest(
            eventType = EventType.CLICK.name,
            sessionId = "session-123",
            userId = null,
            metadata = SaveEventDTO.EventMetadataRequest(
                componentId = 1000L,
                elementId = "element-123",
                targetUrl = "https://jeffrey-oh.click"
            )
        )

        val command = request.toCommand()

        every { saveEventUseCase.saveEvent(command) } returns Mono.empty()

        // when
        val result = eventHandler.saveEvent(request)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        verify(exactly = 1) { saveEventUseCase.saveEvent(match { it.sessionId == "session-123" }) }
    }

}