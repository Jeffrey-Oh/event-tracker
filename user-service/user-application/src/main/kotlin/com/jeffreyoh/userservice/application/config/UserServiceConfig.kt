package com.jeffreyoh.userservice.application.config

import com.jeffreyoh.userservice.application.service.CreatePostService
import com.jeffreyoh.userservice.application.service.TogglePostLikeService
import com.jeffreyoh.userservice.port.`in`.CreatePostUseCase
import com.jeffreyoh.userservice.port.`in`.TogglePostLikeUseCase
import com.jeffreyoh.userservice.port.out.EventTrackerPort
import com.jeffreyoh.userservice.port.out.PostCommandPort
import com.jeffreyoh.userservice.port.out.PostLikeCommandPort
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

}