package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArticleContentRepository {
    Flux<ArticleContentModel> findAll();
    Mono<ArticleContentModel> findById(Long id);
    Mono<ArticleContentModel> save(ArticleContentModel model);
    Mono<Void> delete(Long id);
}
