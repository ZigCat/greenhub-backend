package com.github.zigcat.greenhub.user_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.user_provider.application.exceptions.NotFoundAppException;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.UserRepository;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.r2dbc.ReactiveUserRepository;
import com.github.zigcat.greenhub.user_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.ConflictInfrastructureException;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.NotFoundInfrastructureException;
import com.github.zigcat.greenhub.user_provider.infrastructure.mappers.UserMapper;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.user_provider.infrastructure.models.UserModel;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class R2dbcUserRepository implements UserRepository {
    private final ReactiveUserRepository repository;

    public R2dbcUserRepository(ReactiveUserRepository repository) {
        this.repository = repository;
    }



    @Override
    public Flux<UserModel> findAll() {
        return repository.findAll()
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    throw new DatabaseException("User service unavailable");
                });
    }

    @Override
    public Mono<UserModel> findById(Long id) {
        return repository.findById(id)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found User with this ID");
                    }
                    throw new DatabaseException("User service unavailable");
                });
    }

    @Override
    public Mono<UserModel> findByEmail(String email) {
        return repository.findByEmail(email)
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found User with this Username");
                    }
                    throw new DatabaseException("User service unavailable");
                });
    }

    @Override
    public Mono<InfrastructureDTO.UserAuth> findUserByIdWithScopes(Long id) {
        return UserMapper.mapAuthRows(repository.findUserByIdWithScopes(id))
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found User with this ID");
                    }
                    throw new DatabaseException("User service unavailable");
                });
    }

    @Override
    public Mono<InfrastructureDTO.UserAuth> findUserByEmailWithScopes(String email) {
        return UserMapper.mapAuthRows(repository.findUserByEmailWithScopes(email))
                .onErrorMap(e -> {
                    log.error(e.getMessage());
                    if(e instanceof EmptyResultDataAccessException){
                        throw new NotFoundInfrastructureException("Couldn't found User with this Username");
                    }
                    throw new DatabaseException("User service unavailable");
                });
    }

    @Override
    public Mono<UserModel> save(UserModel model) {
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
