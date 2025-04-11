package com.jeffreyoh.eventtracker.api.infrastructure.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleValidation(ex: WebExchangeBindException): Mono<ResponseEntity<Map<String, Any>>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid")
        }

        return Mono.just(
            ResponseEntity.badRequest().body(
                mapOf(
                    "status" to 400,
                    "message" to "Validation failed",
                    "errors" to errors
                )
            )
        )
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleWebInput(ex: ServerWebInputException): Mono<ResponseEntity<Map<String, Any>>> {
        return Mono.just(
            ResponseEntity.badRequest().body(
                mapOf(
                    "status" to 400,
                    "message" to "Invalid request body",
                    "details" to ex.message
                )
            )
        )
    }

    @ExceptionHandler(ValidationException::class)
    fun handleCustomValidation(ex: ValidationException): Mono<ResponseEntity<Map<String, Any>>> {
        return Mono.just(
            ResponseEntity.badRequest().body(
                mapOf(
                    "status" to 400,
                    "message" to "Validation failed",
                    "details" to ex.message
                )
            )
        )
    }

}