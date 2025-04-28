package com.github.zigcat.greenhub.auth_provider.domain.interfaces;

import com.github.zigcat.greenhub.auth_provider.infrastructure.models.UserKeyModel;
import reactor.core.publisher.Mono;

public interface UserKeyRepository {
    Mono<UserKeyModel> save(UserKeyModel model);
    Mono<UserKeyModel> findByUsername(String username);
    Mono<UserKeyModel> findById(Long id);
    Mono<Void> delete(String username);
}
