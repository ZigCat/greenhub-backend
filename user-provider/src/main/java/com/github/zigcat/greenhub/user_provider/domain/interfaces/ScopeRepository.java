package com.github.zigcat.greenhub.user_provider.domain.interfaces;

import com.github.zigcat.greenhub.user_provider.infrastructure.models.ScopeModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ScopeRepository {
    Flux<ScopeModel> findAll();
    Mono<ScopeModel> findById(Long id);
    Mono<ScopeModel> save(ScopeModel model);
    Flux<ScopeModel> saveAll(Iterable<ScopeModel> models);
    Mono<Void> delete(Long id);
}
