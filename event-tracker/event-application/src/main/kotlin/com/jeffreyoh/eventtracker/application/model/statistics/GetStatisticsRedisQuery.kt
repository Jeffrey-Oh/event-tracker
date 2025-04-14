package com.jeffreyoh.eventtracker.application.model.statistics

import com.jeffreyoh.enums.EventType

data class GetStatisticsRedisQuery(
    val eventType: EventType,
    val componentId: Long,
    val keyword: String? = null,
    val postId: Long? = null,
) {
    companion object {
        fun toQuery(command: StatisticsCommand.GetStatistics): GetStatisticsRedisQuery {
            return GetStatisticsRedisQuery(
                eventType = command.eventType,
                componentId = command.componentId,
                keyword = command.keyword,
                postId = command.postId,
            )
        }
    }
}