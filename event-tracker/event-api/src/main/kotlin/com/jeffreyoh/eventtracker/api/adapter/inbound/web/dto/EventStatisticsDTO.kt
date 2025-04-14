package com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto

import com.jeffreyoh.enums.EventType
import com.jeffreyoh.enums.toEventTypeOrThrow
import com.jeffreyoh.eventtracker.api.infrastructure.exception.ValidationException
import com.jeffreyoh.eventtracker.application.model.statistics.StatisticsCommand
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus

class EventStatisticsDTO {

    data class GetStatisticsRequest(
        @field:NotBlank val eventType: String,
        @field:Min(1) val componentId: Long,
        val keyword: String? = null,
        val postId: Long? = null,
    ) {
        fun toCommand(): StatisticsCommand.GetStatistics {
            val eventType = runCatching { eventType.toEventTypeOrThrow() }
                .getOrElse { throw ValidationException(HttpStatus.BAD_REQUEST, "Invalid event type: $eventType") }

            if (eventType == EventType.SEARCH && keyword.isNullOrBlank()) {
                throw ValidationException(HttpStatus.BAD_REQUEST, "keyword is required for eventType: $eventType")
            }

            if (eventType == EventType.LIKE && postId == null) {
                throw ValidationException(HttpStatus.BAD_REQUEST, "metadata.postId is required for eventType: $eventType")
            }

            return StatisticsCommand.GetStatistics(
                eventType,
                componentId,
                keyword,
                postId,
            )
        }
    }

    data class EventStatisticsResponse(
        val count: Long,
    )

}