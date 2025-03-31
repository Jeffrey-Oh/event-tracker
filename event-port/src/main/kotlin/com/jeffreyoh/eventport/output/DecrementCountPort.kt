package com.jeffreyoh.eventport.output

import reactor.core.publisher.Mono

interface DecrementCountPort {

    fun decrementLikeCount(componentId: Long, postId: Long): Mono<Void>

}