package com.jeffreyoh.eventtracker.port.output

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface RecentSearchRedisPort {

    fun saveRecentKeyword(userId: Long, keyword: String): Mono<Void>
    fun getRecentKeywords(userId: Long): Flux<String>

}