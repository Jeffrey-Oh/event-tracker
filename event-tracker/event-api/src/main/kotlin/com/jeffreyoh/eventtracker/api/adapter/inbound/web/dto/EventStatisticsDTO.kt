package com.jeffreyoh.eventtracker.api.adapter.inbound.web.dto

class EventStatisticsDTO {

    data class EventStatisticsResponse(
        val componentId: Long,
        val count: Long,
    )

}