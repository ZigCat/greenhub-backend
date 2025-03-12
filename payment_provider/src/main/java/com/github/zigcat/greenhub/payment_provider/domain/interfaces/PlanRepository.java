package com.github.zigcat.greenhub.payment_provider.domain.interfaces;

import com.github.zigcat.greenhub.payment_provider.infrastructure.models.PlanModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlanRepository {
    Flux<PlanModel> findAll();
    Mono<PlanModel> findById(Long id);
    Mono<PlanModel> save(PlanModel model);
    Mono<Void> delete(Long id);
}
