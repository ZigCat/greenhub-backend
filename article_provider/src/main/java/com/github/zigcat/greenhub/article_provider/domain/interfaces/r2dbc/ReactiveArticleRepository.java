package com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveArticleRepository extends ReactiveCrudRepository<ArticleModel, Long> {
}
