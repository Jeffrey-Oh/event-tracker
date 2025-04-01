package com.jeffreyoh.eventapi.adapter.inbound.web.dto

import com.jeffreyoh.eventapi.infrastructure.exception.ValidationException
import com.jeffreyoh.eventcore.domain.event.EventCommand
import com.jeffreyoh.eventcore.domain.event.EventMetadata
import com.jeffreyoh.eventcore.domain.event.EventType
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class SaveEventDTO {

    data class SaveEventRequest(
        @field:NotBlank
        val eventType: String,

        val userId: Long?,

        @field:NotBlank
        val sessionId: String,

        @field:Valid
        val metadata: EventMetadataRequest,
    ) {
        fun toCommand(): EventCommand.SaveEventCommand {
            val eventType = eventType.toEventTypeOrThrow()

            if (eventType == EventType.LIKE && userId == null) {
                throw ValidationException(HttpStatus.BAD_REQUEST, "userId is required for eventType: $eventType")
            }

            if (eventType == EventType.LIKE && metadata.postId == null) {
                throw ValidationException(HttpStatus.BAD_REQUEST, "metadata.postId is required for eventType: $eventType")
            }

            return EventCommand.SaveEventCommand(
                eventType,
                userId,
                sessionId,
                EventMetadata(
                    metadata.componentId,
                    metadata.elementId,
                    metadata.keyword,
                    metadata.postId
                )
            )
        }
    }

    data class EventMetadataRequest(
        @field:Min(1) val componentId: Long, // 요소 값이 가변적일 수 있으므로 실질적인 ID 별도 저장
        @field:NotBlank val elementId: String, // 프론트에서 사용하는 요소 값
        val keyword: String? = null,
        val postId: Long? = null,
    )

}

// 확장 함수
fun String.toEventTypeOrThrow(): EventType =
    runCatching { EventType.valueOf(this.uppercase()) }
        .getOrElse { throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid eventType: $this") }
