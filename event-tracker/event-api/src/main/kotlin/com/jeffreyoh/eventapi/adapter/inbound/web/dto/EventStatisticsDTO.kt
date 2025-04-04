package com.jeffreyoh.eventapi.adapter.inbound.web.dto

class EventStatisticsDTO {

    data class EventStatisticsResponse(
        val componentId: Long,
        val count: Long,
    )

}