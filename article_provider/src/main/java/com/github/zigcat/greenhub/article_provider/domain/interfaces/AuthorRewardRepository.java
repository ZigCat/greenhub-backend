package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.domain.AuthorReward;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.AuthorRewardModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AuthorRewardRepository {
    Flux<AuthorRewardModel> findAllByAuthorId(Long authorId);
    Flux<AuthorRewardModel> findAll();
    Mono<AuthorRewardModel> findById(Long id);
    Mono<AuthorRewardModel> save(AuthorRewardModel model);
    Flux<AuthorRewardModel> saveAll(List<AuthorRewardModel> models);
    Mono<Void> delete(Long id);
}
