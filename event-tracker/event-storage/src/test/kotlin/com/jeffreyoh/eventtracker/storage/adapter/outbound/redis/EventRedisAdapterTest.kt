package com.jeffreyoh.eventtracker.storage.adapter.outbound.redis

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.core.domain.event.Event
import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
import com.jeffreyoh.eventtracker.core.domain.event.toJson
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class EventRedisAdapterTest {

    @MockK private lateinit var redisTemplate: ReactiveStringRedisTemplate
    @MockK private lateinit var valueOps: ReactiveValueOperations<String, String>
    private lateinit var eventRedisAdapter: EventRedisAdapter

    @BeforeEach
    fun setUp() {
        eventRedisAdapter = EventRedisAdapter(redisTemplate)

        every { redisTemplate.opsForValue() } returns valueOps
    }

    @ParameterizedTest
    @EnumSource(value = EventType::class, names = ["CLICK", "PAGE_VIEW", "SEARCH"])
    fun `이벤트를 Redis에 저장한다`(eventType: EventType) {
        // given
        val event = Event(
            eventType = eventType,
            userId = 1L,
            sessionId = "session-123",
            metadata = EventMetadata(
                componentId = 1000L,
                elementId = "element-123",
            ),
            createdAt = LocalDateTime.now()
        )

        val keySlot = slot<String>()
        val valueSlot = slot<String>()
        val ttlSlot = slot<Duration>()

        every {
            valueOps.set(capture(keySlot), capture(valueSlot), capture(ttlSlot))
        } returns Mono.empty()

        // when
        val result = eventRedisAdapter.saveToRedis(event)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        val expectedKey = "events:${event.eventType.name.lowercase()}:user:${event.userId}"
        val expectedValue = event.toJson()
        val expectedTtl = Duration.ofMinutes(10)

        assertThat(keySlot.captured).isEqualTo(expectedKey)
        assertThat(valueSlot.captured).isEqualTo(expectedValue)
        assertThat(ttlSlot.captured).isEqualTo(expectedTtl)
    }

}