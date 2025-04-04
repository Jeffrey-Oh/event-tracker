package com.jeffreyoh.userservice.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.jeffreyoh.userservice"])
class UserApiApplication

fun main(args: Array<String>) {
    runApplication<UserApiApplication>(*args)
}
