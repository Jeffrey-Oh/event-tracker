package com.jeffreyoh.userservice.storage.adapter.postgre

import com.jeffreyoh.userservice.core.domain.Post
import com.jeffreyoh.userservice.port.out.PostCommandPort
import com.jeffreyoh.userservice.storage.entity.PostEntity
import com.jeffreyoh.userservice.storage.adapter.postgre.repository.PostRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class PostAdapter(
    private val postRepository: PostRepository,
): PostCommandPort {

    override fun save(post: Post): Mono<Post> {
        return postRepository.save(PostEntity.fromDomain(post))
            .map { postEntity ->
                postEntity.toDomain()
            }
            .switchIfEmpty(Mono.error(IllegalStateException("Save Failed")))
    }

}