package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.Event
import com.jeffreyoh.eventcore.domain.event.EventMetadata
import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventcore.domain.event.toJson
import io.lettuce.core.RestoreArgs.Builder.ttl
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.ValueOperations
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

@ExtendWith(MockKExtension::class)
class RedisEventWriterTest {

    @MockK private lateinit var redisTemplate: ReactiveStringRedisTemplate
    private lateinit var redisEventWriter: RedisEventWriter

    @BeforeEach
    fun setUp() {
        redisEventWriter = RedisEventWriter(redisTemplate)
    }

    @Test
    fun `이벤트를 Redis에 저장한다`() {
        // given
        val event = Event(
            eventType = EventType.CLICK,
            userId = 1L,
            sessionId = "session-123",
            metadata = EventMetadata(
                componentId = 1000L,
                elementId = "element-123",
                targetUrl = "https://jeffrey-oh.click"
            ),
            createdAt = LocalDateTime.now()
        )

        val opsMock = mockk<ReactiveValueOperations<String, String>>()
        every { redisTemplate.opsForValue() } returns opsMock

        val keySlot = slot<String>()
        val valueSlot = slot<String>()
        val ttlSlot = slot<Duration>()

        every {
            opsMock.set(capture(keySlot), capture(valueSlot), capture(ttlSlot))
        } returns Mono.empty()

        // when
        val result = redisEventWriter.saveToRedis(event)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        val expectedKey = "events:${event.eventType.name}:user:${event.userId}:${event.createdAt.toEpochSecond(ZoneOffset.UTC)}"
        val expectedValue = event.toJson()
        val expectedTtl = Duration.ofSeconds(10)

        assertThat(keySlot.captured).isEqualTo(expectedKey)
        assertThat(valueSlot.captured).isEqualTo(expectedValue)
        assertThat(ttlSlot.captured).isEqualTo(expectedTtl)
    }

}