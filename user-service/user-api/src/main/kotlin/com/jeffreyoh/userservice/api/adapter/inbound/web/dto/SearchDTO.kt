package com.jeffreyoh.userservice.api.adapter.inbound.web.dto

class SearchDTO {

    data class RecentSearchByKeywordResponse(
        val keyword: List<String>,
    )

}