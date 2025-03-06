package com.github.zigcat.greenhub.user_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.user_provider.domain.interfaces.ScopeRepository;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.r2dbc.ReactiveScopeRepository;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.user_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.user_provider.infrastructure.models.ScopeModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcScopeRepository implements ScopeRepository {
    private final ReactiveScopeRepository repository;

    public R2dbcScopeRepository(ReactiveScopeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<ScopeModel> findAll() {
        return repository.findAll()
                .onErrorMap(e -> new DatabaseException(e.getMessage()));
    }

    @Override
    public Mono<ScopeModel> findById(Long id) {
        return repository.findById(id)
                .onErrorMap(e -> {
                    if(e instanceof IllegalArgumentException){
                        throw new BadRequestInfrastructureException(e.getMessage());
                    }
                    throw new DatabaseException(e.getMessage());
                });
    }

    @Override
    public Mono<ScopeModel> save(ScopeModel model) {
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
    public Flux<ScopeModel> saveAll(Iterable<ScopeModel> models) {
        return repository.saveAll(models)
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
