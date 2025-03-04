package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
import reactor.core.publisher.Mono;

public interface ArticleContentRepository {
    Mono<ArticleContentModel> findById(Long id);
    Mono<ArticleContentModel> save(ArticleContentModel model);
    Mono<Void> delete(Long id);
}
