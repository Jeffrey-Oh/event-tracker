package com.jeffreyoh.eventapi.adapter.inbound.web.dto

class EventStatisticDTO {

    data class EventStatisticResponse(
        val componentId: Long,
        val count: Long,
    )

}