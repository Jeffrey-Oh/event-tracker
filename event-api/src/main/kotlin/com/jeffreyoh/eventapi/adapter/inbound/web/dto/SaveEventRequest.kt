package com.jeffreyoh.eventapi.adapter.inbound.web.dto

import com.jeffreyoh.eventcore.domain.event.EventCommand
import com.jeffreyoh.eventcore.domain.event.EventMetadata
import com.jeffreyoh.eventcore.domain.event.EventType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

data class SaveEventRequest(
    @field:NotBlank
    val eventType: String,

    val userId: Long?,

    @field:NotBlank
    val sessionId: String,

    @field:NotNull
    val metadata: EventMetadata,
) {
    fun toCommand(): EventCommand.SaveEventCommand {
        return EventCommand.SaveEventCommand(
            eventType.toEventTypeOrThrow(),
            userId,
            sessionId,
            metadata
        )
    }
}

// 확장 함수
fun String.toEventTypeOrThrow(): EventType =
    runCatching { EventType.valueOf(this.uppercase()) }
        .getOrElse { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid eventType: $this") }
