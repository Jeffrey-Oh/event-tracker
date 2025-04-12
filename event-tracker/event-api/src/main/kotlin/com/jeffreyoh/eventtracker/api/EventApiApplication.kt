package com.jeffreyoh.eventtracker.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.jeffreyoh.eventtracker"])
class EventApiApplication

fun main(args: Array<String>) {
    runApplication<EventApiApplication>(*args)
}
