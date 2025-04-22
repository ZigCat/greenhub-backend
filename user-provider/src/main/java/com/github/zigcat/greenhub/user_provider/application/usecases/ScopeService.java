package com.github.zigcat.greenhub.user_provider.application.usecases;

import com.github.zigcat.greenhub.user_provider.application.exceptions.BadRequestAppException;
import com.github.zigcat.greenhub.user_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.user_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.user_provider.domain.Scope;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.ScopeRepository;
import com.github.zigcat.greenhub.user_provider.domain.schemas.ScopeType;
import com.github.zigcat.greenhub.user_provider.infrastructure.mappers.ScopeMapper;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ScopeService {
    private final ScopeRepository repository;
    private final PermissionService permissions;

    public ScopeService(ScopeRepository repository, PermissionService permissions) {
        this.repository = repository;
        this.permissions = permissions;
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

    public Mono<Void> deleteByUserId(Long userId){
        return repository.deleteAllByUserId(userId);
    }

    public Mono<Scope> promote(String scope, Long userId, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        try {
            ScopeType target = ScopeType.fromString(scope);
            if(!permissions.canAccessAccount(auth, userId)){
                return Mono.error(new ForbiddenAppException("Access denied"));
            }
            if(!ScopeType.selfGranted.contains(target) && !auth.isAdmin()){
                return Mono.error(new ForbiddenAppException("Access denied"));
            }
            return repository.findScopesByUserId(userId)
                    .map(ScopeMapper::toEntity)
                    .collectList()
                    .flatMap(scopes -> {
                        boolean match = scopes.stream()
                                .anyMatch(s -> s.getScope().equals(target.getScope()));
                        if(match) {
                            return Mono.error(new BadRequestAppException("User already has this scope"));
                        }
                        Scope newScope = new Scope(userId, target.getScope());
                        return repository.save(ScopeMapper.toModel(newScope)).map(ScopeMapper::toEntity);
                    });
        } catch (IllegalArgumentException e){
            return Mono.error(new BadRequestAppException("Wrong scope param"));
        }
    }

    public Mono<Void> demote(String scope, Long userId, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!auth.isAdmin()) return Mono.error(new ForbiddenAppException("Access denied"));
        try{
            ScopeType target = ScopeType.fromString(scope);
            return repository.findScopesByUserId(userId)
                    .map(ScopeMapper::toEntity)
                    .collectList()
                    .flatMap(scopes -> {
                        boolean match = scopes.stream()
                                .anyMatch(s -> s.getScope().equals(target.getScope()));
                        if(!match) {
                            return Mono.error(new BadRequestAppException("User don't have this scope"));
                        }
                        return repository.deleteByScopeAndUser(userId, scope);
                    });
        } catch(IllegalArgumentException e){
            return Mono.error(new BadRequestAppException("Wrong scope param"));
        }
    }
}
