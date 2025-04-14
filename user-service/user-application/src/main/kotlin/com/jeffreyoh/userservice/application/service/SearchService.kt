package com.jeffreyoh.userservice.application.service

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.userservice.application.model.event.EventTrackerRequest
import com.jeffreyoh.userservice.application.port.out.EventTrackerPort
import com.jeffreyoh.userservice.core.domain.event.EventMetadata
import com.jeffreyoh.userservice.core.domain.post.Post
import com.jeffreyoh.userservice.port.`in`.SearchUseCase
import com.jeffreyoh.userservice.port.out.PostSearchPort
import com.jeffreyoh.userservice.port.out.ReadRedisPort
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

class SearchService(
    private val eventTrackerPort: EventTrackerPort,
    private val postSearchPort: PostSearchPort,
    private val readRedisPort: ReadRedisPort
) : SearchUseCase {

    override fun searchByKeyword(
        userId: Long,
        keyword: String
    ): Flux<Post> {
        return postSearchPort.searchByKeyword(keyword)
            .collectList()
            .flatMap { posts ->
                eventTrackerPort.sendEvent(
                    EventTrackerRequest.SaveEvent(
                        eventType = EventType.SEARCH,
                        userId = userId,
                        sessionId = UUID.randomUUID().toString(),
                        metadata = EventMetadata(
                            componentId = EventType.SEARCH.componentId,
                            elementId = "elementId-$${EventType.SEARCH.groupId}",
                            keyword = keyword,
                            postId = null
                        )
                    )
                ).thenReturn(posts)
            }
            .flatMapMany { Flux.fromIterable(it) }
    }

    override fun recentSearchByKeyword(userId: Long): Mono<List<String>> {
        return readRedisPort.recentSearchByKeyword(userId)
    }

}