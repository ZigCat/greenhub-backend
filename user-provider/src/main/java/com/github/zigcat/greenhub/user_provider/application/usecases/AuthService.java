package com.github.zigcat.greenhub.user_provider.application.usecases;

import com.github.zigcat.greenhub.user_provider.application.exceptions.ForbiddenAppException;
import com.github.zigcat.greenhub.user_provider.application.exceptions.NotFoundAppException;
import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.infrastructure.mappers.UserMapper;
import com.github.zigcat.greenhub.user_provider.application.events.AuthorizeEvent;
import com.github.zigcat.greenhub.user_provider.application.events.LoginEvent;
import com.github.zigcat.greenhub.user_provider.application.events.RegisterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AuthService {
    private final UserService service;

    @Autowired
    public AuthService(UserService service) {
        this.service = service;
    }

    @EventListener
    public void handleRegistrationEvent(RegisterEvent event){
        log.info("Registration event handling initiated");
        AppUser user = UserMapper.toEntity(event.getRequest());
        CompletableFuture<AppUser> replyFuture = event.getReplyFuture();
        log.info("Making database call...");
        service.register(user)
                .doOnSuccess(replyFuture::complete)
                .doOnError(e -> log.error("Error while processing event {}", e.getMessage()))
                .onErrorResume(e -> {
                    replyFuture.completeExceptionally(e);
                    return Mono.empty();
                })
                .subscribe();
    }

    @EventListener
    public void handleAuthorizationEvent(AuthorizeEvent event){
        String username = event.getRequest();
        CompletableFuture<AppUser> replyFuture = event.getReplyFuture();
        service.retrieveByEmailWithScopes(username)
                .doOnSuccess(replyFuture::complete)
                .doOnError(e -> log.error("Error while processing event {}", e.getMessage()))
                .onErrorResume(e -> {
                    replyFuture.completeExceptionally(e);
                    return Mono.empty();
                })
                .subscribe();
    }

    @EventListener
    public void handleLoginEvent(LoginEvent event){
        AppUser request = UserMapper.toEntity(event.getRequest());
        CompletableFuture<AppUser> replyFuture = event.getReplyFuture();
        checkUser(request.getEmail(), request.getPassword())
                .flatMap(user -> service.retrieveByIdWithScopes(user.getId()))
                .doOnSuccess(replyFuture::complete)
                .doOnError(e -> log.error("Error while processing event {}", e.getMessage()))
                .onErrorResume(e -> {
                    replyFuture.completeExceptionally(e);
                    return Mono.empty();
                })
                .subscribe();
    }

    public Mono<AppUser> checkUser(String username, String password){
        return service.retrieveByEmail(username)
                .map(user -> {
                    if(BCrypt.checkpw(password, user.getPassword())){
                        return user;
                    }
                    throw new ForbiddenAppException("Wrong password");
                }).switchIfEmpty(Mono.error(new NotFoundAppException("User not found")));
    }
}
