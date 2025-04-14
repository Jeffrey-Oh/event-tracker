package com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.enums.toEventTypeOrThrow
import com.jeffreyoh.eventtracker.api.infrastructure.exception.ValidationException
import com.jeffreyoh.eventtracker.application.model.event.EventCommand
import com.jeffreyoh.eventtracker.core.domain.event.EventMetadata
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus

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
        fun toCommand(): EventCommand.SaveEvent {
            val eventType = eventType.toEventTypeOrThrow()

            if (eventType == EventType.LIKE && userId == null) {
                throw ValidationException(HttpStatus.BAD_REQUEST, "userId is required for eventType: $eventType")
            }

            if (eventType == EventType.LIKE && metadata.postId == null) {
                throw ValidationException(HttpStatus.BAD_REQUEST, "metadata.postId is required for eventType: $eventType")
            }

            if (eventType == EventType.SEARCH && metadata.keyword.isNullOrBlank()) {
                throw ValidationException(HttpStatus.BAD_REQUEST, "metadata.keyword is required for eventType: $eventType")
            }

            if (metadata.elementId?.isBlank() == true) {
                throw ValidationException(HttpStatus.BAD_REQUEST, "metadata.elementId is required")
            }

            return EventCommand.SaveEvent(
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
        val elementId: String? = null, // 프론트에서 사용하는 요소 값
        val keyword: String? = null,
        val postId: Long? = null,
    )

}