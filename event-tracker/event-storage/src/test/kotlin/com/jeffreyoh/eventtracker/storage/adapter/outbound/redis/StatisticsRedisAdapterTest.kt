package com.jeffreyoh.eventtracker.storage.adapter.outbound.redis

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.eventtracker.application.model.event.EventCommand
import com.jeffreyoh.eventtracker.application.model.event.EventRedisQuery
import com.jeffreyoh.eventtracker.application.model.statistics.GetStatisticsRedisQuery
import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
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
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.data.redis.core.script.RedisScript
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ExtendWith(MockKExtension::class)
class StatisticsRedisAdapterTest {

    @MockK private lateinit var redisTemplate: ReactiveStringRedisTemplate
    @MockK private lateinit var valueOps: ReactiveValueOperations<String, String>
    @MockK private lateinit var valueZSetOps: ReactiveZSetOperations<String, String>
    private lateinit var statisticsRedisAdapter: StatisticsRedisAdapter

    @BeforeEach
    fun setUp() {
        statisticsRedisAdapter = StatisticsRedisAdapter(redisTemplate)
    }

    @ParameterizedTest
    @EnumSource(EventType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNLIKE"])
    fun `이벤트 타입별 Redis 키에서 카운트를 조회한다`(eventType: EventType) {
        // given
        val expectedCount = 500L
        val redisKeySlot = slot<String>()

        every { redisTemplate.opsForValue() } returns valueOps
        every {
            valueOps.get(capture(redisKeySlot))
        } returns Mono.just(expectedCount.toString())

        // when
        val result = statisticsRedisAdapter.getEventCount(
            GetStatisticsRedisQuery(
                eventType = eventType,
                componentId = eventType.componentId,
                keyword = "keyword",
                postId = 1L,
            )
        )

        // then
        StepVerifier.create(result)
            .assertNext {
                assertThat(it).isEqualTo(expectedCount)
            }
            .verifyComplete()

        verify(exactly = 1) { valueOps.get(redisKeySlot.captured) }
    }

    @ParameterizedTest
    @EnumSource(EventType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNLIKE"])
    fun `이벤트 타입별 Redis 키에 대해 카운트를 증가 또는 감소시킨다`(eventType: EventType) {
        // given
        val zsetKeySlot = slot<String>()
        val memberSlot = slot<String>()
        val deltaSlot = slot<Double>()
        val durationSlot = slot<Duration>()

        val command = EventCommand.SaveEvent(
            eventType = eventType,
            userId = 1L,
            sessionId = "sessionId",
            metadata = EventMetadata(
                componentId = eventType.componentId,
                postId = 1L,
                keyword = "keyword",
            )
        )

        every { redisTemplate.opsForZSet() } returns valueZSetOps
        every { valueZSetOps.incrementScore(capture(zsetKeySlot), capture(memberSlot), capture(deltaSlot)) } returns Mono.empty()
        every { redisTemplate.expire(capture(zsetKeySlot), capture(durationSlot)) } returns Mono.empty()

        // when
        val result = statisticsRedisAdapter.saveEventCount(EventRedisQuery.toQuery(command))

        // then
        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun `이벤트 LIKE Redis 키에 대해 카운트를 감소시킨다`() {
        // given
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
        val zsetKey = "statistics:like:$timestamp"
        val member = "component:1003:post:1"

        val keySlot = slot<List<String>>()
        val memberSlot = slot<List<String>>()
        val scriptSlot = slot<RedisScript<Long>>()

        every { redisTemplate.execute(capture(scriptSlot), capture(keySlot), capture(memberSlot)) } returns Flux.just(1L)

        // when
        val result = statisticsRedisAdapter.decrementLike(zsetKey, member)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        assertThat(keySlot.captured.first()).isEqualTo(zsetKey)
        assertThat(memberSlot.captured.first()).isEqualTo(member)
        assertThat(scriptSlot.captured.scriptAsString).contains("redis.call('ZSCORE', KEYS[1], ARGV[1])")
    }

}