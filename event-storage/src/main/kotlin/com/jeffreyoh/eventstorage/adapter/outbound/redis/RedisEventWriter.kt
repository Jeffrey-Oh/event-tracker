package com.jeffreyoh.eventstorage.adapter.outbound.redis

import com.jeffreyoh.eventcore.domain.event.EventCommand
import com.jeffreyoh.eventport.output.SaveEventPort
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Component
class RedisEventWriter: SaveEventPort {

    override fun saveToRedis(command: EventCommand.SaveEventCommand): Mono<Void> {
        log.info { "Saving event to Redis: $command" }
        return Mono.empty()
    }

}