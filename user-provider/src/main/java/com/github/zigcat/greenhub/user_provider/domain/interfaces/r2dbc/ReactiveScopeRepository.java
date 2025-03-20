package com.github.zigcat.greenhub.user_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.user_provider.infrastructure.models.ScopeModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveScopeRepository extends ReactiveCrudRepository<ScopeModel, Long> {
    @Query("SELECT * FROM user_scopes WHERE user_id = :userId")
    Flux<ScopeModel> findScopesByUserId(@Param("userId") Long userId);
    @Query("DELETE FROM user_scopes WHERE user_id = :userId")
    Mono<Void> deleteAllByUserId(@Param("userId") Long userId);
    @Query("DELETE FROM user_scopes WHERE user_id = :userId AND scopes = :scope")
    Mono<Void> deleteByScopeAndUser(@Param("userId") Long userId,
                                    @Param("scope") String scope);
}
