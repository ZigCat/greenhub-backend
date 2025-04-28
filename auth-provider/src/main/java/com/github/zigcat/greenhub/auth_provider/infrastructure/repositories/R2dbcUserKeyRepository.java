package com.github.zigcat.greenhub.auth_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.auth_provider.domain.interfaces.UserKeyRepository;
import com.github.zigcat.greenhub.auth_provider.domain.interfaces.r2dbc.ReactiveUserKeyRepository;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.ConflictInfrastructureException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.NotFoundInfrastructureException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.models.UserKeyModel;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class R2dbcUserKeyRepository implements UserKeyRepository {
    private final ReactiveUserKeyRepository repository;

    public R2dbcUserKeyRepository(ReactiveUserKeyRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<UserKeyModel> save(UserKeyModel model) {
        return repository.save(model)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    } else if(e instanceof ConstraintViolationException){
                        throw new BadRequestInfrastructureException("Constraints rules wasn't satisfied");
                    }
                    throw new DatabaseException("User service unavailable");
                });
    }

    @Override
    public Mono<UserKeyModel> findByUsername(String username) {
        return repository.findByUsername(username)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Not found");
                    }
                    throw new DatabaseException("User service unavailable");
                });
    }

    @Override
    public Mono<UserKeyModel> findById(Long id) {
        return repository.findById(id)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Not found");
                    }
                    throw new DatabaseException("User service unavailable");
                });
    }

    @Override
    public Mono<Void> delete(String username) {
        return repository.deleteByUsername(username)
                .onErrorMap(e -> {
                    if(e instanceof DataIntegrityViolationException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    }
                    throw new DatabaseException("User service unavailable");
                });
    }
}
