package com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository

import com.jeffreyoh.userservice.storage.entity.PostEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface PostRepository : ReactiveCrudRepository<PostEntity, Long> {
}