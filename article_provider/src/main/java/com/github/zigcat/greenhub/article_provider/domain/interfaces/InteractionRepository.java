package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.domain.Interaction;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.InteractionModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InteractionRepository {
    InteractionModel toModel(Interaction entity);
    Interaction toEntity(InteractionModel model);
    Flux<InteractionModel> findAll();
    Mono<InteractionModel> save(InteractionModel model);
    Mono<Void> delete(String id);
}
