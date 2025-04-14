package com.jeffreyoh.userservice.application.config

import com.jeffreyoh.userservice.application.port.`in`.CreatePostUseCase
import com.jeffreyoh.userservice.application.port.`in`.SearchUseCase
import com.jeffreyoh.userservice.application.port.`in`.TogglePostLikeUseCase
import com.jeffreyoh.userservice.application.port.out.*
import com.jeffreyoh.userservice.application.service.CreatePostService
import com.jeffreyoh.userservice.application.service.SearchService
import com.jeffreyoh.userservice.application.service.TogglePostLikeService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserServiceConfig {

    @Bean
    fun createPostUseCase(
        postCommandPort: PostCommandPort
    ) : CreatePostUseCase =
        CreatePostService(postCommandPort)

    @Bean
    fun togglePostLikeUseCase(
        postLikeCommandPort: PostLikeCommandPort,
        eventTrackerPort: EventTrackerPort
    ) : TogglePostLikeUseCase =
        TogglePostLikeService(
            postLikeCommandPort,
            eventTrackerPort
        )

    @Bean
    fun searchUseCase(
        eventTrackerPort: EventTrackerPort,
        postSearchPort: PostSearchPort,
        readRedisPost: RedisReadPort,
        redisCommandPort: RedisCommandPort
    ) : SearchUseCase =
        SearchService(
            eventTrackerPort,
            postSearchPort,
            readRedisPost,
            redisCommandPort
    )

}