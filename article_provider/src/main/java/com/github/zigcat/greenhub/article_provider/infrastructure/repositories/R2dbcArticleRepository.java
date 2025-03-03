package com.github.zigcat.greenhub.article_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc.ReactiveArticleRepository;
import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcArticleRepository implements ArticleRepository {
    private final ReactiveArticleRepository repository;

    public R2dbcArticleRepository(ReactiveArticleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<ArticleModel> findAll() {
        return repository
                .findAll()
                .onErrorMap(e -> new DatabaseException(e.getMessage()));
    }

    @Override
    public Flux<ArticleModel> findAllByStatus(ArticleStatus articleStatus) {
        return repository.findAllByArticleStatus(articleStatus)
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }

    @Override
    public Mono<ArticleModel> findById(Long id) {
        return repository
                .findById(id)
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }

    @Override
    public Flux<ArticleModel> findAllById(Iterable<Long> ids) {
        return repository
                .findAllById(ids)
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException
                            || e instanceof ClassCastException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }

    @Override
    public Mono<ArticleModel> save(ArticleModel model) {
        return repository
                .save(model)
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException
                            || e instanceof DataIntegrityViolationException
                            || e instanceof DuplicateKeyException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository
                .deleteById(id)
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException
                            || e instanceof EmptyResultDataAccessException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }
}
