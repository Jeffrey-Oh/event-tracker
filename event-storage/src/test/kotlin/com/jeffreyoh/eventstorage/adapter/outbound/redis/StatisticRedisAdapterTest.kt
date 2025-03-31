package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.EventType
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
class StatisticRedisAdapterTest {

    private lateinit var redisTemplate: ReactiveRedisTemplate<String, Long>
    private lateinit var valueOps: ReactiveValueOperations<String, Long>
    private lateinit var getStatisticCountPort: StatisticRedisAdapter
    private lateinit var incrementCountPort: StatisticRedisAdapter
    private lateinit var decrementCountPort: StatisticRedisAdapter

    @BeforeEach
    fun setUp() {
        redisTemplate = mockk()
        valueOps = mockk()
        getStatisticCountPort = StatisticRedisAdapter(redisTemplate)
        incrementCountPort = StatisticRedisAdapter(redisTemplate)
        decrementCountPort = StatisticRedisAdapter(redisTemplate)

        every { redisTemplate.opsForValue() } returns valueOps
    }

    @ParameterizedTest
    @EnumSource(EventType::class)
    fun `이벤트 타입별 Redis 키에서 카운트를 조회한다`(eventType: EventType) {
        // given
        val componentId = 1000L
        val expectedCount = 500L
        val redisKey = "statistics:${eventType.name.lowercase()}:component:$componentId"

        every { valueOps.get(redisKey) } returns Mono.just(expectedCount)

        // when
        val result = getStatisticCountPort.getCount(componentId, eventType)

        // then
        StepVerifier.create(result)
            .assertNext {
                assertThat(it).isEqualTo(expectedCount)
            }
            .verifyComplete()
    }

    @ParameterizedTest
    @EnumSource(EventType::class, mode = EnumSource.Mode.EXCLUDE, names = ["LIKE"])
    fun `이벤트 LIKE를 제외한 타입별 Redis 키에 대해 카운트를 증가시킨다`(eventType: EventType) {
        // given
        val componentId = 1000L
        val redisKey = "statistics:${eventType.name.lowercase()}:component:$componentId"

        every { valueOps.increment(redisKey) } returns Mono.empty()

        // when
        val result = incrementCountPort.incrementCount(componentId, eventType)

        // then
        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun `이벤트 LIKE Redis 키에 대해 카운트를 증가시킨다`() {
        // given
        val componentId = 1000L
        val postId = 1L
        val redisKey = "statistics:${EventType.LIKE.name.lowercase()}:component:$componentId:post:$postId"

        every { valueOps.increment(redisKey) } returns Mono.empty()

        // when
        val result = incrementCountPort.incrementLikeCount(componentId, postId)

        // then
        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun `이벤트 LIKE Redis 키에 대해 카운트를 감소시킨다`() {
        // given
        val componentId = 1000L
        val postId = 1L
        val redisKey = "statistics:${EventType.LIKE.name.lowercase()}:component:$componentId:post:$postId"

        every { valueOps.decrement(redisKey) } returns Mono.empty()

        // when
        val result = decrementCountPort.decrementLikeCount(componentId, postId)

        // then
        StepVerifier.create(result)
            .verifyComplete()
    }

}