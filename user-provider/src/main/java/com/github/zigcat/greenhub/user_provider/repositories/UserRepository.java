package com.github.zigcat.greenhub.user_provider.repositories;

import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<AppUser, Long> {
    Mono<AppUser> findByEmail(String email);
}
