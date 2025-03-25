package com.jeffreyoh.eventapi.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.ReactiveRedisTemplate
import java.util.function.Consumer

private val log = KotlinLogging.logger {}

@Configuration
class RedisConfig(
    val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        reactiveRedisTemplate.opsForValue().get("1")
            .doOnSuccess(Consumer { i: String? -> log.info { "Initialize redis connection" } })
            .doOnError(Consumer { err: Throwable -> log.error { "Failed initialize redis connection : ${err.message}" } })
            .subscribe()
    }

}