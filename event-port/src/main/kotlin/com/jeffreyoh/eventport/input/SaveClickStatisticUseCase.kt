package com.jeffreyoh.eventport.input

import reactor.core.publisher.Mono

interface SaveClickStatisticUseCase {

    fun saveClick(componentId: Long): Mono<Void>

}