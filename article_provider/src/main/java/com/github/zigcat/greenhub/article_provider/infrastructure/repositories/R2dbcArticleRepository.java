package com.github.zigcat.greenhub.article_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc.ReactiveArticleRepository;
import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.ConflictInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.NotFoundInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class R2dbcArticleRepository implements ArticleRepository {
    private final ReactiveArticleRepository repository;

    public R2dbcArticleRepository(ReactiveArticleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<ArticleModel> findAll() {
        return repository.findAll()
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Flux<ArticleModel> findAllByStatus(String articleStatus) {
        return repository.findAllByStatus(articleStatus)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found article with this status");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Flux<ArticleModel> findAllByCreator(Long creator) {
        return repository.findAllByCreator(creator)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found article with this creator");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Mono<ArticleModel> findById(Long id) {
        return repository
                .findById(id)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found article with this ID");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Flux<ArticleModel> findAllById(Iterable<Long> ids) {
        return repository
                .findAllById(ids)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found articles with this IDs");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Mono<ArticleModel> save(ArticleModel model) {
        return repository
                .save(model)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    } else if(e instanceof ConstraintViolationException){
                        throw new BadRequestInfrastructureException("Constraints rules wasn't satisfied");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository
                .deleteById(id)
                .onErrorMap(e -> {
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }
}
