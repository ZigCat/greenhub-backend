package com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ReactiveArticleRepository extends ReactiveCrudRepository<ArticleModel, Long> {
    @Query("SELECT * FROM articles WHERE article_status = :status")
    Flux<ArticleModel> findAllByStatus(@Param("status") String status);
}
