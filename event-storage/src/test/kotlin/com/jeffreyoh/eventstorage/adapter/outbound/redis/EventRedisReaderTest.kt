package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventport.output.ReadEventPort
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
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
class EventRedisReaderTest {

    @MockK private lateinit var redisTemplate: ReactiveStringRedisTemplate
    private lateinit var valueOps: ReactiveValueOperations<String, String>
    private lateinit var readEventPort: EventRedisReader

    @BeforeEach
    fun setUp() {
        valueOps = mockk()
        readEventPort = EventRedisReader(redisTemplate)

        every { redisTemplate.opsForValue() } returns valueOps
    }

    @Test
    fun `이벤트를 Redis에서 읽는다`() {
        // given
        val key = "events:like:user:1:post:1"

        val keySlot = slot<String>()
        val expectedValue = "event-data"
        every { valueOps.get(capture(keySlot)) } returns Mono.just(expectedValue)

        // when
        val result = readEventPort.readLikeFromRedisKey(key)

        // then
        StepVerifier.create(result)
            .expectNext(expectedValue)
            .verifyComplete()

        assertThat(keySlot.captured).isEqualTo(key)
    }

}