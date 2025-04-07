package com.jeffreyoh.userservice.api.adapter.inbound.web.dto

class SearchDTO {

    data class SearchRequest(
        val userId: Long,
        val keyword: String,
    )

}