package com.jeffreyoh.userport

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UserPortApplication

fun main(args: Array<String>) {
	runApplication<UserPortApplication>(*args)
}
