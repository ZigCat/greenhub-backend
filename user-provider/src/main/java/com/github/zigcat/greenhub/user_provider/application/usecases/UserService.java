package com.github.zigcat.greenhub.user_provider.application.usecases;

import com.github.zigcat.greenhub.user_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.user_provider.application.exceptions.NotFoundAppException;
import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.user_provider.domain.Scope;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.UserRepository;
import com.github.zigcat.greenhub.user_provider.infrastructure.mappers.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ScopeService scopeService;
    private final PermissionService permissions;

    public UserService(
            UserRepository userRepository,
            ScopeService scopeService,
            PermissionService permissions
    ) {
        this.userRepository = userRepository;
        this.scopeService = scopeService;
        this.permissions = permissions;
    }

    public Flux<AppUser> list(){
        return userRepository.findAll()
                .map(model -> UserMapper.toEntity(model, null));
    }

    public Mono<AppUser> retrieve(Long userId){
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this ID not found")))
                .map(model -> UserMapper.toEntity(model, null));
    }

    public Mono<AppUser> retrieveByEmail(String email){
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this email not found")))
                .map(model -> UserMapper.toEntity(model, null));
    }

    public Mono<AppUser> retrieveByIdWithScopes(Long id){
        return userRepository.findUserByIdWithScopes(id)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this ID not found")))
                .map(UserMapper::toEntity);
    }

    public Mono<AppUser> retrieveByEmailWithScopes(String email){
        return userRepository.findUserByEmailWithScopes(email)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this email not found")))
                .map(UserMapper::toEntity);
    }

    public Mono<AppUser> register(AppUser user){
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
        return userRepository.save(UserMapper.toModel(user))
                            .flatMap(model -> {
                                List<Scope> defaultScopes = Scope.defaultScopes(model.getId());
                                return scopeService.saveAll(defaultScopes)
                                        .collectList()
                                        .map(scopes -> UserMapper.toEntity(model, scopes));
                            })
                            .doOnError(e -> log.error("Error while saving user to DB ", e));
    }

    public Mono<AppUser> update(Long id, AppUser user, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!permissions.canAccessAccount(auth, id)) return Mono.error(new ForbiddenAppException("Access denied"));
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this ID not found")))
                .flatMap(model -> {
                    model.setFname(Optional.ofNullable(user.getFname()).orElse(user.getFname()));
                    model.setLname(Optional.ofNullable(user.getLname()).orElse(user.getLname()));
                    model.setEmail(Optional.ofNullable(user.getEmail()).orElse(user.getEmail()));
                    if(user.getPassword() != null){
                        model.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
                    }
                    return userRepository.save(model)
                            .map(saved -> UserMapper.toEntity(model, null));
                });
    }

    public Mono<Void> delete(Long id, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!permissions.canAccessAccount(auth, id)) return Mono.error(new ForbiddenAppException("Access Denied"));
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this ID not found")))
                .flatMap(model -> userRepository.delete(model.getId()));
    }
}
