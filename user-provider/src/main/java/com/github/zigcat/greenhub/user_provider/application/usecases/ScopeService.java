package com.github.zigcat.greenhub.user_provider.application.usecases;

import com.github.zigcat.greenhub.user_provider.domain.Scope;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.ScopeRepository;
import com.github.zigcat.greenhub.user_provider.infrastructure.mappers.ScopeMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ScopeService {
    private final ScopeRepository repository;

    public ScopeService(ScopeRepository repository) {
        this.repository = repository;
    }

    public Flux<Scope> list(){
        return repository.findAll()
                .map(ScopeMapper::toEntity);
    }

    public Mono<Scope> retrieve(Long id){
        return repository.findById(id)
                .map(ScopeMapper::toEntity);
    }

    public Mono<Scope> create(Scope scope){
        return repository.save(ScopeMapper.toModel(scope))
                .map(ScopeMapper::toEntity);
    }

    public Flux<Scope> saveAll(List<Scope> scopes){
        return repository.saveAll(scopes
                .stream()
                .map(ScopeMapper::toModel)
                .toList())
                .map(ScopeMapper::toEntity);
    }
}
