package com.jeffreyoh.eventapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.jeffreyoh"])
class EventApiApplication

fun main(args: Array<String>) {
    runApplication<EventApiApplication>(*args)
}
