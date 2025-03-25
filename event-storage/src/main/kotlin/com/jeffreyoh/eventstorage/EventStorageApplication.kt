package com.jeffreyoh.eventstorage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventStorageApplication

fun main(args: Array<String>) {
    runApplication<EventStorageApplication>(*args)
}
