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

        fun getRecentKeywordKey(userId: Long) = RECENT_KEYWORD_KEY.format(userId)
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