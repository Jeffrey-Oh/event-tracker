package com.jeffreyoh.userservice.core.domain

enum class EventType(
    val eventName: String,
    val description: String,
    val groupId: Long,
    val componentId: Long,
) {
    PAGE_VIEW(
        eventName = "page_view",
        description = "페이지 조회",
        groupId = 1L,
        componentId = 1000L
    ),
    SEARCH(
        eventName = "search",
        description = "검색",
        groupId = 2L,
        componentId = 1001L
    ),
    CLICK(
        eventName = "click",
        description = "클릭",
        groupId = 3L,
        componentId = 1002L
    ),
    LIKE(
        eventName = "like",
        description = "좋아요",
        groupId = 4L,
        componentId = 1003L
    ),
    UNLIKE(
        eventName = "like",
        description = "싫어요",
        groupId = 4L,
        componentId = 1003L
    ),
}