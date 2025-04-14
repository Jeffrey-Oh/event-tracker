package com.jeffreyoh.eventtracker.application.model.statistics

import com.jeffreyoh.enums.EventType

class StatisticsCommand {

    data class GetStatistics(
        val eventType: EventType,
        val componentId: Long,
        val keyword: String? = null,
        val postId: Long? = null,
    )

}