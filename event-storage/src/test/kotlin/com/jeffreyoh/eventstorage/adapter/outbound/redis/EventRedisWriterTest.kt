package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.Event
import com.jeffreyoh.eventcore.domain.event.EventMetadata
import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventcore.domain.event.toJson
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
class EventRedisWriterTest {

    @MockK private lateinit var redisTemplate: ReactiveStringRedisTemplate
    private lateinit var valueOps: ReactiveValueOperations<String, String>
    private lateinit var saveEventPort: EventRedisWriter

    @BeforeEach
    fun setUp() {
        valueOps = mockk()
        saveEventPort = EventRedisWriter(redisTemplate)

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
                targetUrl = "https://jeffrey-oh.click"
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
        val result = saveEventPort.saveToRedis(event)

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

    @Test
    fun `이벤트 LIKE를 Redis에 저장한다`() {
        // given
        val event = Event(
            eventType = EventType.LIKE,
            userId = 1L,
            sessionId = "session-123",
            metadata = EventMetadata(
                componentId = 1000L,
                elementId = "element-123",
                targetUrl = "https://jeffrey-oh.click",
                postId = 1L
            ),
            createdAt = LocalDateTime.now()
        )

        val expectedKey = "events:${event.eventType.name.lowercase()}:user:${event.userId!!}:post:${event.metadata.postId}"

        val keySlot = slot<String>()
        val valueSlot = slot<String>()

        every {
            valueOps.set(capture(keySlot), capture(valueSlot))
        } returns Mono.empty()

        // when
        val result = saveEventPort.saveLikeEventToRedis(expectedKey, event)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        val expectedValue = event.toJson()

        assertThat(keySlot.captured).isEqualTo(expectedKey)
        assertThat(valueSlot.captured).isEqualTo(expectedValue)
    }

    @Test
    fun `이벤트를 Redis에 삭제한다`() {
        // given
        val event = Event(
            eventType = EventType.LIKE,
            userId = 1L,
            sessionId = "session-123",
            metadata = EventMetadata(
                componentId = 1000L,
                elementId = "element-123",
                targetUrl = "https://jeffrey-oh.click",
                postId = 1L
            ),
            createdAt = LocalDateTime.now()
        )

        val keySlot = slot<String>()
        every { redisTemplate.delete(capture(keySlot)) } returns Mono.just(1L)

        val expectedKey = "events:${event.eventType.name.lowercase()}:user:${event.userId!!}:post:${event.metadata.postId}"

        // when
        val result = saveEventPort.deleteFromRedisKey(expectedKey)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        verify(exactly = 1) { redisTemplate.delete(capture(keySlot)) }

        assertThat(keySlot.captured).isEqualTo(expectedKey)
    }

}