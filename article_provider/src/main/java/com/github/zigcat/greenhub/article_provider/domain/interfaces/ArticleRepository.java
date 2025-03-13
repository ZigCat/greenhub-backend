package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArticleRepository {
    Flux<ArticleModel> findAll();
    Flux<ArticleModel> findAllByStatus(String articleStatus);
    Mono<ArticleModel> findById(Long id);
    Flux<ArticleModel> findAllById(Iterable<Long> ids);
    Mono<ArticleModel> save(ArticleModel model);
    Mono<Void> delete(Long id);
}
