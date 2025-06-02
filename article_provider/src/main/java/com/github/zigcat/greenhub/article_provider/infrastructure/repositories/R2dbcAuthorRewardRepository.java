package com.github.zigcat.greenhub.article_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.AuthorRewardRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc.ReactiveAuthorRewardRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.ConflictInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.NotFoundInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.AuthorRewardModel;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class R2dbcAuthorRewardRepository implements AuthorRewardRepository {
    private final ReactiveAuthorRewardRepository repository;

    public R2dbcAuthorRewardRepository(ReactiveAuthorRewardRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<AuthorRewardModel> findAllByAuthorId(Long authorId) {
        return repository.findAllByAuthorId(authorId)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found reward for this author");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Flux<AuthorRewardModel> findAll() {
        return repository.findAll()
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Mono<AuthorRewardModel> findById(Long id) {
        return repository
                .findById(id)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found reward with this ID");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Flux<AuthorRewardModel> findAllByCalculatedAtAfter(LocalDateTime date) {
        return repository.findAllByCalculatedAtAfter(date)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Mono<AuthorRewardModel> save(AuthorRewardModel model) {
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
    public Flux<AuthorRewardModel> saveAll(List<AuthorRewardModel> models){
        return repository
                .saveAll(models)
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
