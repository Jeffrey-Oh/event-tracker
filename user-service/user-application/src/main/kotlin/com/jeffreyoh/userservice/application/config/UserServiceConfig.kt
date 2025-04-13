package com.jeffreyoh.userservice.application.config

import com.jeffreyoh.userservice.application.service.CreatePostService
import com.jeffreyoh.userservice.application.service.SearchService
import com.jeffreyoh.userservice.application.service.TogglePostLikeService
import com.jeffreyoh.userservice.port.`in`.CreatePostUseCase
import com.jeffreyoh.userservice.port.`in`.SearchUseCase
import com.jeffreyoh.userservice.port.`in`.TogglePostLikeUseCase
import com.jeffreyoh.userservice.port.out.*
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
        readRedisPort: ReadRedisPort,
        commandRedisPort: CommandRedisPort
    ) : TogglePostLikeUseCase =
        TogglePostLikeService(
            postLikeCommandPort,
            eventTrackerPort,
            readRedisPort,
            commandRedisPort
        )

    @Bean
    fun searchUseCase(
        eventTrackerPort: EventTrackerPort,
        postSearchPort: PostSearchPort,
        readRedisPost: ReadRedisPort
    ) : SearchUseCase =
        SearchService(
            eventTrackerPort,
            postSearchPort,
            readRedisPost
    )

}