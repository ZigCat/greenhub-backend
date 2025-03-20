package com.github.zigcat.greenhub.user_provider.application.usecases;

import com.github.zigcat.greenhub.user_provider.application.exceptions.*;
import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.user_provider.domain.Scope;
import com.github.zigcat.greenhub.user_provider.domain.interfaces.UserRepository;
import com.github.zigcat.greenhub.user_provider.domain.schemas.Role;
import com.github.zigcat.greenhub.user_provider.exceptions.ClientErrorException;
import com.github.zigcat.greenhub.user_provider.exceptions.ServerErrorException;
import com.github.zigcat.greenhub.user_provider.infrastructure.mappers.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                .map(model -> UserMapper.toEntity(model, null))
                .onErrorResume(e -> {
                    log.error("An error occurred: {}", e.getMessage());
                    if(e instanceof ServerErrorException){
                        return Flux.error(new ServerErrorException(e.getMessage(), ((ServerErrorException) e).getCode()));
                    }
                    return Mono.error(new ServerErrorAppException("An unexpected error occurred"));
                });
    }

    public Mono<AppUser> retrieve(Long userId){
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this ID not found")))
                .flatMap(model -> {
                    try {
                        return Mono.just(UserMapper.toEntity(model, null));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .onErrorResume(e -> {
                    log.error("An error occurred: {}", e.getMessage());
                    if(e instanceof ClientErrorException){
                        return Mono.error(new ClientErrorException(e.getMessage(), ((ClientErrorException) e).getCode()));
                    } else if(e instanceof ServerErrorException){
                        return Mono.error(new ServerErrorException(e.getMessage(), ((ServerErrorException) e).getCode()));
                    }
                    return Mono.error(new ServerErrorAppException("An unexpected error occurred"));
                });
    }

    public Mono<AppUser> retrieveByEmail(String email){
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this email not found")))
                .map(model -> UserMapper.toEntity(model, null))
                .onErrorResume(e -> {
                    log.error("An error occurred: {}", e.getMessage());
                    if(e instanceof ClientErrorException ce){
                        return Mono.error(new ClientErrorException(ce.getMessage(), ce.getCode()));
                    } else if(e instanceof ServerErrorException se){
                        return Mono.error(new ServerErrorException(se.getMessage(), se.getCode()));
                    }
                    return Mono.error(new ServerErrorAppException("An unexpected error occurred"));
                });
    }

    public Mono<AppUser> retrieveByIdWithScopes(Long id){
        return userRepository.findUserByIdWithScopes(id)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this ID not found")))
                .map(UserMapper::toEntity)
                .onErrorResume(e -> {
                    log.error("An error occurred: {}", e.getMessage());
                    if(e instanceof ClientErrorException){
                        return Mono.error(new ClientErrorException(e.getMessage(), ((ClientErrorException) e).getCode()));
                    } else if(e instanceof ServerErrorException){
                        return Mono.error(new ServerErrorException(e.getMessage(), ((ServerErrorException) e).getCode()));
                    }
                    return Mono.error(new ServerErrorAppException("An unexpected error occurred"));
                });
    }

    public Mono<AppUser> retrieveByEmailWithScopes(String email){
        return userRepository.findUserByEmailWithScopes(email)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this email not found")))
                .map(UserMapper::toEntity)
                .onErrorResume(e -> {
                    log.error("An error occurred: {}", e.getMessage());
                    if(e instanceof ClientErrorException){
                        return Mono.error(new ClientErrorException(e.getMessage(), ((ClientErrorException) e).getCode()));
                    } else if(e instanceof ServerErrorException){
                        return Mono.error(new ServerErrorException(e.getMessage(), ((ServerErrorException) e).getCode()));
                    }
                    return Mono.error(new ServerErrorAppException("An unexpected error occurred"));
                });
    }

    @Transactional
    public Mono<AppUser> register(AppUser user){
        if(!EmailValidator.getInstance().isValid(user.getEmail())){
            return Mono.error(new BadRequestAppException("Invalid email address"));
        }
        if(isPasswordInvalid(user.getPassword())){
            return Mono.error(new BadRequestAppException("Password must contain at least 8 symbols, one number, and one special character"));
        }
        return userRepository.findByEmail(user.getEmail())
            .flatMap(existingUser -> Mono.error(new ConflictAppException("User with this email already exists")))
            .switchIfEmpty(Mono.defer(() -> {
                user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
                user.setRole(Role.USER);
                return userRepository.save(UserMapper.toModel(user))
                        .flatMap(model -> {
                            List<Scope> defaultScopes = Scope.defaultScopes(model.getId());
                            return scopeService.saveAll(defaultScopes)
                                    .collectList()
                                    .map(scopes -> UserMapper.toEntity(model, scopes));
                        });
            }))
            .cast(AppUser.class)
            .onErrorResume(e -> {
                log.error("An error occurred: {}", e.getMessage());
                if(e instanceof ClientErrorException){
                    return Mono.error(new ClientErrorException(e.getMessage(), ((ClientErrorException) e).getCode()));
                } else if(e instanceof ServerErrorException){
                    return Mono.error(new ServerErrorException(e.getMessage(), ((ServerErrorException) e).getCode()));
                }
                return Mono.error(new ServerErrorAppException("An unexpected error occurred"));
            });
    }

    @Transactional
    public Mono<AppUser> update(Long id, AppUser user, ServerHttpRequest request){
        if(user.getEmail() != null){
            if(!EmailValidator.getInstance().isValid(user.getEmail())){
                return Mono.error(new BadRequestAppException("Invalid email address"));
            }
        }
        if(user.getPassword() != null){
            if(isPasswordInvalid(user.getPassword())){
                return Mono.error(new BadRequestAppException("Password must contain at least 8 symbols, one number, and one special character"));
            }
        }
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!permissions.canAccessAccount(auth, id)) return Mono.error(new ForbiddenAppException("Access denied"));
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this ID not found")))
                .flatMap(model -> {
                    model.setFname(Optional.ofNullable(user.getFname()).orElse(model.getFname()));
                    model.setLname(Optional.ofNullable(user.getLname()).orElse(model.getLname()));
                    model.setEmail(Optional.ofNullable(user.getEmail()).orElse(model.getEmail()));
                    if(user.getPassword() != null){
                        model.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
                    }
                    return userRepository.save(model)
                            .map(saved -> UserMapper.toEntity(model, null));
                })
                .onErrorResume(e -> {
                    log.error("An error occurred: {}", e.getMessage());
                    if(e instanceof ClientErrorException){
                        return Mono.error(new ClientErrorException(e.getMessage(), ((ClientErrorException) e).getCode()));
                    } else if(e instanceof ServerErrorException){
                        return Mono.error(new ServerErrorException(e.getMessage(), ((ServerErrorException) e).getCode()));
                    }
                    return Mono.error(new ServerErrorAppException("An unexpected error occurred"));
                });
    }

    @Transactional
    public Mono<Void> delete(Long id, ServerHttpRequest request){
        AuthorizationData auth = permissions.extractAuthData(request);
        if(!permissions.canAccessAccount(auth, id)) return Mono.error(new ForbiddenAppException("Access Denied"));
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundAppException("User with this ID not found")))
                .flatMap(model -> scopeService.deleteByUserId(model.getId())
                        .then(userRepository.delete(model.getId())))
                .onErrorResume(e -> {
                    log.error("An error occurred: {}", e.getMessage());
                    if(e instanceof ClientErrorException){
                        return Mono.error(new ClientErrorException(e.getMessage(), ((ClientErrorException) e).getCode()));
                    } else if(e instanceof ServerErrorException){
                        return Mono.error(new ServerErrorException(e.getMessage(), ((ServerErrorException) e).getCode()));
                    }
                    return Mono.error(new ServerErrorAppException("An unexpected error occurred"));
                });
    }

    private boolean isPasswordInvalid(String password) {
        String regex = "^(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[a-z\\d@$!%*?&]{8,}$";
        return password == null || !password.matches(regex);
    }

}
