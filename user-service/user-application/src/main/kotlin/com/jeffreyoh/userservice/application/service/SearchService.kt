package com.jeffreyoh.userservice.application.service

import com.jeffreyoh.userservice.core.domain.EventTrackerCommand
import com.jeffreyoh.userservice.core.domain.Post
import com.jeffreyoh.userservice.port.`in`.SearchUseCase
import com.jeffreyoh.userservice.port.out.EventTrackerPort
import com.jeffreyoh.userservice.port.out.PostSearchPort
import com.jeffreyoh.userservice.port.out.ReadRedisPort
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
                eventTrackerPort.sendSearchEvent(
                    EventTrackerCommand.SearchCommand(
                        eventType = EventTrackerCommand.EventType.SEARCH,
                        userId = userId,
                        keyword = keyword
                    )
                ).thenReturn(posts)
            }
            .flatMapMany { Flux.fromIterable(it) }
    }

    override fun recentSearchByKeyword(userId: Long): Mono<List<String>> {
        return readRedisPort.recentSearchByKeyword(userId)
    }

}