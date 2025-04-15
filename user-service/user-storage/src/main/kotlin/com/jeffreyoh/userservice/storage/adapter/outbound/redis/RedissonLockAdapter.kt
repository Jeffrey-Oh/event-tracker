package com.jeffreyoh.userservice.storage.adapter.outbound.redis

import com.jeffreyoh.userservice.application.port.out.DistributedLockPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

@Component
class RedissonLockAdapter(
    private val redissonReactiveClient: RedissonReactiveClient
) : DistributedLockPort {

    override fun <T> withLockReactive(
        key: String,
        waitTimeSec: Long,
        leaseTimeSec: Long,
        block: () -> Mono<T>
    ): Mono<T> {
        val lock = redissonReactiveClient.getLock(key)
        val startTime = System.nanoTime()

        return lock.tryLock(waitTimeSec, leaseTimeSec, TimeUnit.SECONDS)
            .flatMap { locked ->
                when {
                    locked -> {
                        log.info { "락 획득 성공: key=$key" }
                        block()
                            .flatMap { result ->
                                lock.unlock()
                                    .doOnSuccess { log.info { "락 해제 성공: key=$key" } }
                                    .then(Mono.justOrEmpty(result))
                            }
                            .onErrorResume { e ->
                                lock.unlock()
                                    .doOnSuccess { log.info { "락 해제 성공(에러 후): key=$key" } }
                                    .then(Mono.error(e))
                            }
                    }
                    else -> {
                        log.warn { "락 획득 실패: key=$key" }
                        Mono.error(IllegalStateException("락 획득 실패: $key"))
                    }
                }
            }
            .doOnError { e ->
                log.error { "락 처리 실패: key=$key, error=${e.message}" }
            }
            .doFinally {
                val durationMs = (System.nanoTime() - startTime) / 1_000_000
                log.info { "락 처리 시간: ${durationMs}ms for key=$key" }
            }
    }
}