package com.jeffreyoh.userservice.application.port.out

import reactor.core.publisher.Mono

interface DistributedLockPort {

    fun <T> withLock(key: String, waitTimeSec: Long = 5, leaseTimeSec: Long = 5, block: () -> Mono<T>): Mono<T>

}