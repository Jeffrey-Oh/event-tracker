package com.jeffreyoh.eventtracker.core.domain.event

data class EventMetadata(
    val componentId: Long, // 요소 값이 가변적일 수 있으므로 실질적인 ID 별도 저장
    val elementId: String? = null, // 프론트에서 사용하는 요소 값
    val keyword: String? = null,
    val postId: Long? = null,
)
