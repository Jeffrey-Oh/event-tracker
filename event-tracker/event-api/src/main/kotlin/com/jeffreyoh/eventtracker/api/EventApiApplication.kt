package com.jeffreyoh.eventtracker.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.jeffreyoh.eventtracker"], exclude = [R2dbcAutoConfiguration::class])
class EventApiApplication

fun main(args: Array<String>) {
    runApplication<EventApiApplication>(*args)
}
