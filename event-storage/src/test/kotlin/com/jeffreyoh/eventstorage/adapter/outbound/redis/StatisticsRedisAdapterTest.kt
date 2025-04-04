package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.EventType
import com.jeffreyoh.eventport.output.StatisticsRedisPort
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.script.RedisScript
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
class StatisticsRedisAdapterTest {

    @MockK private lateinit var redisTemplate: ReactiveRedisTemplate<String, Long>
    @MockK private lateinit var valueOps: ReactiveValueOperations<String, Long>
    private lateinit var statisticsRedisAdapter: StatisticsRedisPort

    @BeforeEach
    fun setUp() {
        statisticsRedisAdapter = StatisticsRedisAdapter(redisTemplate)
    }

    @ParameterizedTest
    @EnumSource(EventType::class, mode = EnumSource.Mode.EXCLUDE, names = ["UNLIKE"])
    fun `이벤트 타입별 Redis 키에서 카운트를 조회한다`(eventType: EventType) {
        // given
        val componentId = 1000L
        val expectedCount = 500L
        var redisKey = "statistics:${eventType.name.lowercase()}:component:$componentId"
        if (eventType == EventType.LIKE) redisKey += ":post:1" // postId는 임의로 설정

        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.get(redisKey) } returns Mono.just(expectedCount)

        // when
        val result = when(eventType) {
            EventType.CLICK -> statisticsRedisAdapter.getClickCount(componentId)
            EventType.PAGE_VIEW -> statisticsRedisAdapter.getPageViewCount(componentId)
            EventType.SEARCH -> statisticsRedisAdapter.getSearchCount(componentId)
            EventType.LIKE -> statisticsRedisAdapter.getLikeCount(componentId, 1L) // postId는 임의로 설정
            else -> Mono.empty()
        }

        // then
        StepVerifier.create(result)
            .assertNext {
                assertThat(it).isEqualTo(expectedCount)
            }
            .verifyComplete()
    }

    @ParameterizedTest
    @EnumSource(EventType::class)
    fun `이벤트 LIKE를 제외한 타입별 Redis 키에 대해 카운트를 증가시킨다`(eventType: EventType) {
        // given
        val componentId = 1000L
        var redisKey = "statistics:${eventType.name.lowercase()}:component:$componentId"
        if (eventType == EventType.LIKE) redisKey += ":post:1" // postId는 임의로 설정

        every { redisTemplate.opsForValue() } returns valueOps
        every { valueOps.increment(redisKey) } returns Mono.empty()

        // when
        val result = when(eventType) {
            EventType.CLICK -> statisticsRedisAdapter.incrementClick(componentId)
            EventType.PAGE_VIEW -> statisticsRedisAdapter.incrementPageView(componentId)
            EventType.SEARCH -> statisticsRedisAdapter.incrementSearch(componentId)
            EventType.LIKE -> statisticsRedisAdapter.incrementLike(componentId, 1L) // postId는 임의로 설정
            else -> Mono.empty()
        }

        // then
        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun `이벤트 LIKE Redis 키에 대해 카운트를 감소시킨다`() {
        // given
        val componentId = 1000L
        val postId = 1L
        val keySlot = slot<List<String>>()
        val scriptSlot = slot<RedisScript<Long>>()

        every { redisTemplate.execute(capture(scriptSlot), capture(keySlot)) } returns Flux.just(1L)

        // when
        val result = statisticsRedisAdapter.decrementLike(componentId, postId)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        assertThat(keySlot.captured.first()).isEqualTo("statistics:like:component:$componentId:post:$postId")
        assertThat(scriptSlot.captured.scriptAsString).contains("redis.call('GET', KEYS[1])")
    }

}