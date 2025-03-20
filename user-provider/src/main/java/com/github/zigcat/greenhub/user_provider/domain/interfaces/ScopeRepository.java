package com.github.zigcat.greenhub.user_provider.domain.interfaces;

import com.github.zigcat.greenhub.user_provider.infrastructure.models.ScopeModel;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScopeRepository {
    Flux<ScopeModel> findAll();
    Mono<ScopeModel> findById(Long id);
    Flux<ScopeModel> findScopesByUserId(Long userId);
    Mono<ScopeModel> save(ScopeModel model);
    Flux<ScopeModel> saveAll(Iterable<ScopeModel> models);
    Mono<Void> delete(Long id);
    Mono<Void> deleteAllByUserId(Long userId);
    Mono<Void> deleteByScopeAndUser(Long userId, String scope);
}
