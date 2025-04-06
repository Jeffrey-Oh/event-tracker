package com.jeffreyoh.userservice.storage.adapter.postgre.repository

import com.jeffreyoh.userservice.storage.entity.PostLikeEntity
import reactor.core.publisher.Mono

interface PostLikeCustomRepository {

    fun save(postLikeEntity: PostLikeEntity): Mono<PostLikeEntity>

}