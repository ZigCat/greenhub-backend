package com.github.zigcat.greenhub.auth_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.auth_provider.infrastructure.models.UserKeyModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ReactiveUserKeyRepository extends ReactiveCrudRepository<UserKeyModel, Long> {
    Mono<UserKeyModel> findByUsername(String username);

    @Query("DELETE FROM user_key WHERE username = :username")
    Mono<Void> deleteByUsername(String username);
}
