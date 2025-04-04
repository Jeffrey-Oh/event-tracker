package com.jeffreyoh.eventstorage.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import java.util.function.Consumer

private val log = KotlinLogging.logger {}

@Component
@EnableR2dbcAuditing
@EnableR2dbcRepositories
class R2dbcConfig(
    val databaseClient: DatabaseClient
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        databaseClient.sql("SELECT 1").fetch().one()
            .subscribe(
                Consumer { _: MutableMap<String, Any> -> log.info { "Initialize r2dbc connection." } },
                Consumer { _: Throwable -> log.error { "Failed to initialize r2dbc connection." } }
            )
    }

}