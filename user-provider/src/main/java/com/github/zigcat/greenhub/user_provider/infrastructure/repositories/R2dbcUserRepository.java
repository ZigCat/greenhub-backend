package com.github.zigcat.greenhub.user_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.user_provider.domain.interfaces.UserRepository;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.r2dbc.ReactiveUserRepository;
import com.github.zigcat.greenhub.user_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.user_provider.infrastructure.mappers.UserMapper;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.user_provider.infrastructure.models.UserModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcUserRepository implements UserRepository {
    private final ReactiveUserRepository repository;

    public R2dbcUserRepository(ReactiveUserRepository repository) {
        this.repository = repository;
    }



    @Override
    public Flux<UserModel> findAll() {
        return repository.findAll()
                .onErrorMap(e -> new DatabaseException(e.getMessage()));
    }

    @Override
    public Mono<UserModel> findById(Long id) {
        return repository.findById(id)
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }

    @Override
    public Mono<UserModel> findByEmail(String email) {
        return repository.findByEmail(email)
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }

    @Override
    public Mono<InfrastructureDTO.UserAuth> findUserByIdWithScopes(Long id) {
        return UserMapper.mapAuthRows(repository.findUserByIdWithScopes(id))
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException
                            || e instanceof DataIntegrityViolationException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }

    @Override
    public Mono<InfrastructureDTO.UserAuth> findUserByEmailWithScopes(String email) {
        return UserMapper.mapAuthRows(repository.findUserByEmailWithScopes(email))
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException
                            || e instanceof DataIntegrityViolationException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }

    @Override
    public Mono<UserModel> save(UserModel model) {
        return repository.save(model)
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException
                            || e instanceof DataIntegrityViolationException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id)
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException
                            || e instanceof EmptyResultDataAccessException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }
}
