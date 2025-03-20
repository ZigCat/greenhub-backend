package com.github.zigcat.greenhub.user_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.user_provider.domain.interfaces.ScopeRepository;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.r2dbc.ReactiveScopeRepository;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.ConflictInfrastructureException;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.NotFoundInfrastructureException;
import com.github.zigcat.greenhub.user_provider.infrastructure.models.ScopeModel;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class R2dbcScopeRepository implements ScopeRepository {
    private final ReactiveScopeRepository repository;

    public R2dbcScopeRepository(ReactiveScopeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<ScopeModel> findAll() {
        return repository.findAll()
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    throw new DatabaseException("Scope service unavailable");
                });
    }

    @Override
    public Mono<ScopeModel> findById(Long id) {
        return repository.findById(id)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found Scope with this ID");
                    }
                    throw new DatabaseException("Scope service unavailable");
                });
    }

    @Override
    public Flux<ScopeModel> findScopesByUserId(Long userId) {
        return repository.findScopesByUserId(userId)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found Scope with this User ID");
                    }
                    throw new DatabaseException("Scope service unavailable");
                });
    }

    @Override
    public Mono<ScopeModel> save(ScopeModel model) {
        return repository.save(model)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    } else if(e instanceof ConstraintViolationException){
                        throw new BadRequestInfrastructureException("Constraints rules wasn't satisfied");
                    }
                    throw new DatabaseException("Scope service unavailable");
                });
    }

    @Override
    public Flux<ScopeModel> saveAll(Iterable<ScopeModel> models) {
        return repository.saveAll(models)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    } else if(e instanceof ConstraintViolationException){
                        throw new BadRequestInfrastructureException("Constraints rules wasn't satisfied");
                    }
                    throw new DatabaseException("Scope service unavailable");
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id)
                .onErrorMap(e -> {
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    }
                    throw new DatabaseException("Scope service unavailable");
                });
    }

    @Override
    public Mono<Void> deleteAllByUserId(Long userId) {
        return repository.deleteAllByUserId(userId)
                .onErrorMap(e -> {
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    }
                    throw new DatabaseException("Scope service unavailable");
                });
    }

    @Override
    public Mono<Void> deleteByScopeAndUser(Long userId, String scope) {
        return repository.deleteByScopeAndUser(userId, scope)
                .onErrorMap(e -> {
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    }
                    throw new DatabaseException("Scope service unavailable");
                });
    }
}
