package com.jeffreyoh.userservice.storage.adapter.outbound.postgre

import com.jeffreyoh.userservice.core.domain.post.Post
import com.jeffreyoh.userservice.application.port.out.PostCommandPort
import com.jeffreyoh.userservice.application.port.out.PostSearchPort
import com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository.PostCustomRepository
import com.jeffreyoh.userservice.storage.adapter.outbound.postgre.repository.PostRepository
import com.jeffreyoh.userservice.storage.entity.PostEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class PostAdapter(
    private val postRepository: PostRepository,
    private val postCustomRepository: PostCustomRepository
): PostCommandPort, PostSearchPort {

    override fun save(post: Post): Mono<Post> {
        return postRepository.save(PostEntity.fromDomain(post))
            .map { postEntity -> postEntity.toDomain() }
            .switchIfEmpty(Mono.error(IllegalStateException("Save Failed")))
    }

    override fun searchByKeyword(
        keyword: String,
        limit: Int
    ): Flux<Post> {
        return postCustomRepository.searchByKeyword(keyword, limit)
            .map { postEntity -> postEntity.toDomain() }
            .switchIfEmpty(Flux.empty())
    }

}