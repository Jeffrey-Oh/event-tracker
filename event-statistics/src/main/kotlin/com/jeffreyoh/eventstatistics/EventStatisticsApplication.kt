package com.jeffreyoh.eventstatistics

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventStatisticsApplication

fun main(args: Array<String>) {
    runApplication<EventStatisticsApplication>(*args)
}
