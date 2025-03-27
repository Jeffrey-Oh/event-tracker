package com.jeffreyoh.eventstorage.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.util.function.Consumer

private val log = KotlinLogging.logger {}

@Configuration
class RedisConfig(
    val redisConnectionFactory: ReactiveRedisConnectionFactory,
    val reactiveRedisTemplate: ReactiveStringRedisTemplate
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        reactiveRedisTemplate.opsForValue().get("1")
            .doOnSuccess(Consumer { i: String? -> log.info { "Initialize redis connection" } })
            .doOnError(Consumer { err: Throwable -> log.error { "Failed initialize redis connection : ${err.message}" } })
            .subscribe()
    }

    @Bean
    fun reactiveLongRedisTemplate(): ReactiveRedisTemplate<String, Long> {
        val context = RedisSerializationContext
            .newSerializationContext<String, Long>(StringRedisSerializer())
            .value(Jackson2JsonRedisSerializer(Long::class.java))
            .build()

        return ReactiveRedisTemplate(redisConnectionFactory, context)
    }

}