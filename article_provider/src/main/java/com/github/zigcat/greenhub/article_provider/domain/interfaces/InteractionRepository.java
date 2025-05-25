package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.InteractionModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface InteractionRepository {
    Flux<InteractionModel> findAll();
    Flux<InteractionModel> findByArticleId(Long articleId);
    Flux<InteractionModel> findAllByArticleIds(List<Long> articleIds);
    Mono<InteractionModel> findByUserAndArticle(Long userId, Long articleId);
    Mono<InteractionModel> upsert(Long userId, Long articleId, Integer like, Integer views, Double rating);
}
