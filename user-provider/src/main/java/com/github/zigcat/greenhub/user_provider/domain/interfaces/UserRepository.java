package com.github.zigcat.greenhub.user_provider.domain.interfaces;

import com.github.zigcat.greenhub.user_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.user_provider.infrastructure.models.UserModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Flux<UserModel> findAll();
    Mono<UserModel> findById(Long id);
    Mono<UserModel> findByEmail(String email);
    Mono<InfrastructureDTO.UserAuth> findUserByIdWithScopes(Long id);
    Mono<InfrastructureDTO.UserAuth> findUserByEmailWithScopes(String email);
    Mono<UserModel> save(UserModel model);
    Mono<Void> delete(Long id);
}
