package com.jeffreyoh.userservice.storage.adapter.outbound.redis

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockKExtension::class)
class RedisReadAdapterTest {

    @MockK private lateinit var redisTemplate: ReactiveStringRedisTemplate
    @MockK private lateinit var listOps: ReactiveListOperations<String, String>
    private lateinit var redisReadAdapter: RedisReadAdapter

    @BeforeEach
    fun setUp() {
        redisReadAdapter = RedisReadAdapter(
            redisTemplate
        )

        every { redisTemplate.opsForList() } returns listOps
    }

    companion object {
        private const val RECENT_KEYWORD_KEY = "recent:search:user:%d"
        private const val LIKE_CHECK_KEY = "like:user:%d:post:%d"

        fun getRecentKeywordKey(userId: Long) = RECENT_KEYWORD_KEY.format(userId)
        fun getLikeCheckKey(userId: Long, postId: Long) = LIKE_CHECK_KEY.format(userId, postId)
    }

    @Test
    fun `사용자의 좋아요 상태를 확인한다`() {
        // given
        val userId = 1L
        val postId = 1L

        val keySlot = slot<String>()
        val expectedValue = "true"

        every {
            redisTemplate.opsForValue().get(capture(keySlot))
        } returns Mono.just(expectedValue)

        // when
        val result = redisReadAdapter.getLikeCheck(userId, postId)

        // then
        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete()

        verify(exactly = 1) { redisTemplate.opsForValue().get(keySlot.captured) }

        val expectedKey = getLikeCheckKey(userId, postId)

        assertThat(keySlot.captured).isEqualTo(expectedKey)
    }

    @Test
    fun `최근 키워드 최대 10개를 불러온다`() {
        // given
        val userId = 1L
        val keySlot = slot<String>()
        val expectedKeyword = listOf("keyword1", "keyword2")

        every {
            listOps.range(capture(keySlot), 0, -1)
        } returns Flux.fromIterable(expectedKeyword)

        // when
        val result = redisReadAdapter.recentSearchByKeyword(userId)

        // then
        StepVerifier.create(result)
            .expectNext(expectedKeyword)
            .verifyComplete()

        verify(exactly = 1) { listOps.range(keySlot.captured, 0, -1) }

        val expectedKey = getRecentKeywordKey(userId)

        assertThat(keySlot.captured).isEqualTo(expectedKey)
    }

}