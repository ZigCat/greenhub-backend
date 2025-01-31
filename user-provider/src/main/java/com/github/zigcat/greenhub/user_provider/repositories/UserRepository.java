package com.github.zigcat.greenhub.user_provider.repositories;

import com.github.zigcat.greenhub.user_provider.dto.mq.responses.UserAuthResponse;
import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import io.r2dbc.spi.Row;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<AppUser, Long> {
    Mono<AppUser> findByEmail(String email);

    @Query("SELECT u.user_id, u.email, u.role, s.scopes FROM users u LEFT JOIN user_scopes s ON u.user_id = s.user_id WHERE u.user_id = :userId")
    Flux<UserAuthResponse> findUserByIdWithScopes(@Param("userId") Long userId);

    @Query("SELECT u.user_id, u.email, u.role, s.scopes FROM users u LEFT JOIN user_scopes s ON u.user_id = s.user_id WHERE u.email = :username")
    Flux<UserAuthResponse> findUserByEmailWithScopes(@Param("username") String username);
}
