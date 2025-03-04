package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.InteractionModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InteractionRepository {
    Flux<InteractionModel> findAll();
    Flux<InteractionModel> findByArticleId(Long articleId);
    Mono<InteractionModel> upsert(Long userId, Long articleId, Integer like, Integer views, Double rating);
}
