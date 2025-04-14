package com.jeffreyoh.eventtracker.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ReactorResourceFactory
import reactor.netty.resources.LoopResources

@Configuration
class NettyWorkerCustomizer {

    @Bean
    fun reactorResourceFactory(): ReactorResourceFactory {
        val factory = ReactorResourceFactory()
        factory.isUseGlobalResources = false  // ← 핵심!
        factory.loopResources = LoopResources.create(
            "event-loop",   // 쓰레드 이름 prefix
            1,              // selector thread 수 (보통 1)
            16,             // worker thread 수 (변경할 값)
            true            // daemon thread 여부
        )
        return factory
    }

}