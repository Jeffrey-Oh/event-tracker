package com.jeffreyoh.eventtracker.api.infrastructure.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class ValidationException(
    status: HttpStatus,
    message: String? = null,
) : ResponseStatusException(
    status,
    message,
) {
}