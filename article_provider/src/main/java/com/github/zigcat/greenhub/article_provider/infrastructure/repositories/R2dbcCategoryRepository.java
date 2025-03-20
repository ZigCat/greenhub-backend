package com.github.zigcat.greenhub.article_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.CategoryRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc.ReactiveCategoryRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.ConflictInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.NotFoundInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.CategoryModel;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class R2dbcCategoryRepository implements CategoryRepository {
    private final ReactiveCategoryRepository repository;

    public R2dbcCategoryRepository(ReactiveCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<CategoryModel> findAll() {
        return repository.findAll()
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Mono<CategoryModel> findById(Long id) {
        return repository.findById(id)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found category with this ID");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Mono<CategoryModel> save(CategoryModel model) {
        return repository.save(model)
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
        return repository.deleteById(id)
                .onErrorMap(e -> {
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    }
                    throw new DatabaseException("User service unavailable");
                });
    }
}
