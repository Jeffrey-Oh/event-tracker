package com.jeffreyoh.eventtracker.application.port.out

import reactor.core.publisher.Mono

interface RecentSearchRedisPort {

    fun saveRecentKeyword(userId: Long, keyword: String): Mono<Void>
    fun getRecentKeywords(userId: Long): Mono<List<String>>

}