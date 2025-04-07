package com.jeffreyoh.userservice.storage.adapter.postgre.repository

import com.jeffreyoh.userservice.storage.entity.PostEntity
import reactor.core.publisher.Flux

interface PostCustomRepository {

    fun searchByKeyword(keyword: String, limit: Int): Flux<PostEntity>

}