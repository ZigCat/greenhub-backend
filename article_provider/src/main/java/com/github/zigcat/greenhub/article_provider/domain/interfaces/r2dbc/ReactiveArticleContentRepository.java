package com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ReactiveArticleContentRepository extends ReactiveMongoRepository<ArticleContentModel, String> {
    Mono<ArticleContentModel> findByArticleId(Long articleId);
    Mono<Void> deleteByArticleId(Long articleId);
}
