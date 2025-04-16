package com.jeffreyoh.userservice.storage.adapter.outbound.redis

import com.jeffreyoh.userservice.application.port.out.DistributedLockPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private val log = KotlinLogging.logger {}

@Component
class RedissonLockAdapter(
    private val redissonClient: RedissonClient
) : DistributedLockPort {

    override fun <T> withLock(
        key: String,
        waitTimeSec: Long,
        leaseTimeSec: Long,
        block: () -> Mono<T>
    ): Mono<T> {
        val lock = redissonClient.getLock(key)
        val startTime = System.nanoTime()
        val maxRetries = 2
        val baseDelayMs = 500L

        return tryLockWithRetry(lock, key, waitTimeSec, leaseTimeSec, block, 1, maxRetries, baseDelayMs)
            .doOnError { e ->
                log.error(e) { "💥 락 처리 실패: key=$key, error=${e.message}, thread=${Thread.currentThread().name}" }
            }
            .doFinally {
                val durationMs = (System.nanoTime() - startTime) / 1_000_000
                log.info { "⏱️ 락 처리 시간: ${durationMs}ms for key=$key" }
            }
    }

    private fun <T> tryLockWithRetry(
        lock: org.redisson.api.RLock,
        key: String,
        waitTimeSec: Long,
        leaseTimeSec: Long,
        block: () -> Mono<T>,
        attempt: Int,
        maxRetries: Int,
        baseDelayMs: Long
    ): Mono<T> {
        return Mono.fromCallable {
            try {
                log.debug { "🔒 tryLock 호출: key=$key, attempt=$attempt, waitTimeSec=$waitTimeSec, thread=${Thread.currentThread().name}" }
                lock.tryLock(waitTimeSec, leaseTimeSec, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                log.warn(e) { "⚠️ 락 획득 중 인터럽트: key=$key, attempt=$attempt, thread=${Thread.currentThread().name}" }
                Thread.currentThread().interrupt() // 인터럽트 상태 복구
                throw e
            }
        }
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap { locked ->
                if (locked) {
                    log.info { "🔐 락 획득 성공: key=$key, attempt=$attempt, thread=${Thread.currentThread().name}" }
                    block()
                        .flatMap { result ->
                            Mono.fromCallable {
                                if (lock.isHeldByCurrentThread) {
                                    lock.unlock()
                                    log.info { "🔓 락 해제 성공: key=$key, thread=${Thread.currentThread().name}" }
                                }
                                result
                            }.subscribeOn(Schedulers.boundedElastic())
                        }
                        .onErrorResume { e ->
                            log.error(e) { "❌ 블록 실행 중 에러 → 락 해제 시도: key=$key" }
                            Mono.fromCallable {
                                if (lock.isHeldByCurrentThread) {
                                    lock.unlock()
                                    log.info { "🔓 락 해제 성공(에러 후): key=$key, thread=${Thread.currentThread().name}" }
                                }
                                throw e
                            }.subscribeOn(Schedulers.boundedElastic())
                        }
                } else if (attempt < maxRetries) {
                    val delayMs = baseDelayMs + Random.nextLong(0, 50)
                    log.debug { "🔄 재시도 대기: key=$key, attempt=$attempt, delayMs=$delayMs" }
                    Mono.delay(Duration.ofMillis(delayMs), Schedulers.parallel())
                        .then(tryLockWithRetry(lock, key, waitTimeSec, leaseTimeSec, block, attempt + 1, maxRetries, baseDelayMs))
                } else {
                    log.warn { "🚫 락 획득 최종 실패: key=$key, waitTimeSec=$waitTimeSec, thread=${Thread.currentThread().name}" }
                    Mono.error(IllegalStateException("락 획득 실패: $key"))
                }
            }
            .onErrorResume(InterruptedException::class.java) { e ->
                log.warn(e) { "🚫 인터럽트로 재시도 중단: key=$key, attempt=$attempt" }
                Mono.error(IllegalStateException("락 획득 중 인터럽트: $key", e))
            }
    }

}