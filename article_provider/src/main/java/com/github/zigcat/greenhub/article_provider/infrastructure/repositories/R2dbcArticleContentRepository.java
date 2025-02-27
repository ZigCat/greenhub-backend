package com.github.zigcat.greenhub.article_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleContentRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc.ReactiveArticleContentRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcArticleContentRepository implements ArticleContentRepository {
    private final ReactiveArticleContentRepository repository;

    public R2dbcArticleContentRepository(ReactiveArticleContentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<ArticleContentModel> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<ArticleContentModel> findById(Long id) {
        return repository.findByArticleId(id);
    }

    @Override
    public Mono<ArticleContentModel> save(ArticleContentModel model) {
        return repository.save(model);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.deleteByArticleId(id);
    }
}
