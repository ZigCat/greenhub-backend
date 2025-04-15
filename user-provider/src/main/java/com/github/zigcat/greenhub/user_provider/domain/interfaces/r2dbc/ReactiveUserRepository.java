package com.github.zigcat.greenhub.user_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.user_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.user_provider.infrastructure.models.UserModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveUserRepository extends ReactiveCrudRepository<UserModel, Long> {
    Mono<UserModel> findByEmail(String email);

    @Query("SELECT u.*, s.scopes FROM users u LEFT JOIN user_scopes s ON u.user_id = s.user_id WHERE u.user_id = :userId")
    Flux<InfrastructureDTO.UserAuth> findUserByIdWithScopes(@Param("userId") Long userId);

    @Query("SELECT u.*, s.scopes FROM users u LEFT JOIN user_scopes s ON u.user_id = s.user_id WHERE u.email = :username")
    Flux<InfrastructureDTO.UserAuth> findUserByEmailWithScopes(@Param("username") String username);
}
