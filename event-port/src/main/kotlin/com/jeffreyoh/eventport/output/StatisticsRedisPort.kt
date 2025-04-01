package com.jeffreyoh.eventport.output

import reactor.core.publisher.Mono

interface StatisticsRedisPort {

    fun getLikeCount(componentId: Long, postId: Long): Mono<Long>
    fun getClickCount(componentId: Long): Mono<Long>
    fun getPageViewCount(componentId: Long): Mono<Long>
    fun getSearchCount(componentId: Long): Mono<Long>

    fun incrementClick(componentId: Long): Mono<Void>
    fun incrementPageView(componentId: Long): Mono<Void>
    fun incrementSearch(componentId: Long): Mono<Void>

    fun incrementLike(componentId: Long, postId: Long): Mono<Void>
    fun decrementLike(componentId: Long, postId: Long): Mono<Void>

}