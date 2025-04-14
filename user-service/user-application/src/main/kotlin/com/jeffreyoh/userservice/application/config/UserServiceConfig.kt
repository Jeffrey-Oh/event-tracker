package com.jeffreyoh.userservice.application.config

import com.jeffreyoh.userservice.application.port.`in`.CreatePostUseCase
import com.jeffreyoh.userservice.application.port.`in`.TogglePostLikeUseCase
import com.jeffreyoh.userservice.application.port.out.EventTrackerPort
import com.jeffreyoh.userservice.application.port.out.RedisCommandPort
import com.jeffreyoh.userservice.application.port.out.RedisReadPort
import com.jeffreyoh.userservice.application.service.CreatePostService
import com.jeffreyoh.userservice.application.service.SearchService
import com.jeffreyoh.userservice.application.service.TogglePostLikeService
import com.jeffreyoh.userservice.port.`in`.SearchUseCase
import com.jeffreyoh.userservice.port.out.PostCommandPort
import com.jeffreyoh.userservice.port.out.PostLikeCommandPort
import com.jeffreyoh.userservice.port.out.PostSearchPort
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
        eventTrackerPort: EventTrackerPort,
        redisReadPort: RedisReadPort,
        redisCommandPort: RedisCommandPort
    ) : TogglePostLikeUseCase =
        TogglePostLikeService(
            postLikeCommandPort,
            eventTrackerPort,
            redisReadPort,
            redisCommandPort
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