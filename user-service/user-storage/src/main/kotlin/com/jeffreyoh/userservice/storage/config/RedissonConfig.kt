package com.jeffreyoh.userservice.storage.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val log = KotlinLogging.logger {}

@Configuration
class RedissonConfig(
    @Value("\${spring.data.redis.host:localhost}") private val redisHost: String,
    @Value("\${spring.data.redis.port:6379}") private val redisPort: Int,
) {

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config().apply {
            useSingleServer().apply {
                address = "redis://$redisHost:$redisPort"
                connectTimeout = 5000 // 5초 타임아웃
                retryAttempts = 3 // 재시도 3회
                retryInterval = 1000 // 재시도 간격 1초
            }
        }

        return try {
            val client = Redisson.create(config)
            log.info { "Redisson 클라이언트 초기화 성공: $redisHost:$redisPort" }
            client
        } catch (e: Exception) {
            log.error { "Redisson 클라이언트 초기화 실패: ${e.message}" }
            throw e
        }
    }

}