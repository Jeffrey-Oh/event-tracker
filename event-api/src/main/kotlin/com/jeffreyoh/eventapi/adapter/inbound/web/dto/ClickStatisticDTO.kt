package com.jeffreyoh.eventapi.adapter.inbound.web.dto

class ClickStatisticDTO {

    data class ClistStatisticResponse(
        val componentId: Long,
        val count: Long,
    )

}