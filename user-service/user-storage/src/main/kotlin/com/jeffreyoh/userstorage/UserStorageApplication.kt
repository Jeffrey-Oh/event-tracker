package com.jeffreyoh.userstorage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class UserStorageApplication

fun main(args: Array<String>) {
	runApplication<UserStorageApplication>(*args)
}
