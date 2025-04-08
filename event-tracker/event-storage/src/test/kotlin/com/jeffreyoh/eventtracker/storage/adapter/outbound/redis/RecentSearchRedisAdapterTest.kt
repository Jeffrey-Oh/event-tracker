package com.jeffreyoh.eventtracker.storage.adapter.outbound.redis

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
import java.time.Duration

@ExtendWith(MockKExtension::class)
class RecentSearchRedisAdapterTest {

    @MockK
    private lateinit var redisTemplate: ReactiveStringRedisTemplate
    @MockK
    private lateinit var listOps: ReactiveListOperations<String, String>
    private lateinit var recentSearchRedisAdapter: RecentSearchRedisAdapter

    @BeforeEach
    fun setUp() {
        recentSearchRedisAdapter = RecentSearchRedisAdapter(redisTemplate)

        every { redisTemplate.opsForList() } returns listOps
    }

    companion object {
        private const val RECENT_KEYWORD_LIMIT = 10
        private const val TTL_HOURS = 1L
        private const val KEY = "recent:search:user:%d"
    }

    private fun getKey(userId: Long): String = KEY.format(userId)

    @Test
    fun `최근 키워드 최대 10개까지 저장한다`() {
        // given
        val userId = 1L
        val keyword = "keyword"
        val keywordSlot = slot<String>()
        val keySlot = slot<String>()

        every {
            listOps.remove(capture(keySlot), 0, capture(keywordSlot))
                .then(listOps.leftPush(capture(keySlot), capture(keywordSlot)))
                .then(listOps.trim(capture(keySlot), 0, RECENT_KEYWORD_LIMIT - 1L))
                .then(redisTemplate.expire(capture(keySlot), Duration.ofHours(TTL_HOURS)))
        } returns Mono.empty()

        // when
        val result = recentSearchRedisAdapter.saveRecentKeyword(userId, keyword)

        // then
        StepVerifier.create(result)
            .verifyComplete()

        verify(exactly = 1) { listOps.remove(keySlot.captured, 0, keywordSlot.captured) }
        verify(exactly = 1) { listOps.leftPush(keySlot.captured, keywordSlot.captured) }
        verify(exactly = 1) { listOps.trim(keySlot.captured, 0, RECENT_KEYWORD_LIMIT - 1L) }
        verify(exactly = 1) { redisTemplate.expire(keySlot.captured, Duration.ofHours(TTL_HOURS)) }

        val expectedKey = getKey(userId)

        assertThat(keySlot.captured).isEqualTo(expectedKey)
        assertThat(keywordSlot.captured).isEqualTo(keyword)
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
        val result = recentSearchRedisAdapter.getRecentKeywords(userId)

        // then
        StepVerifier.create(result)
            .expectNext(expectedKeyword)
            .verifyComplete()

        verify(exactly = 1) { listOps.range(keySlot.captured, 0, -1) }

        val expectedKey = getKey(userId)

        assertThat(keySlot.captured).isEqualTo(expectedKey)
    }

}