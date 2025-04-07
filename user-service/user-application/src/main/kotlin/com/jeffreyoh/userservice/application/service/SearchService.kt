package com.jeffreyoh.userservice.application.service

import com.jeffreyoh.userservice.core.domain.EventTrackerCommand
import com.jeffreyoh.userservice.core.domain.Post
import com.jeffreyoh.userservice.port.`in`.SearchUseCase
import com.jeffreyoh.userservice.port.out.EventTrackerPort
import com.jeffreyoh.userservice.port.out.PostSearchPort
import reactor.core.publisher.Flux

class SearchService(
    private val eventTrackerPort: EventTrackerPort,
    private val postSearchPort: PostSearchPort
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

}