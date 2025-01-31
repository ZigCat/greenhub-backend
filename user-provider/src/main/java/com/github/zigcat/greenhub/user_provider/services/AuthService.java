package com.github.zigcat.greenhub.user_provider.services;

import com.github.zigcat.greenhub.user_provider.dto.mq.requests.LoginRequest;
import com.github.zigcat.greenhub.user_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.user_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.user_provider.dto.mq.responses.UserAuthResponse;
import com.github.zigcat.greenhub.user_provider.dto.rest.entities.UserDTO;
import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import com.github.zigcat.greenhub.user_provider.events.events.AuthorizeMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.events.events.LoginMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.events.replies.AuthorizeAuthServiceReply;
import com.github.zigcat.greenhub.user_provider.events.replies.RegisterAuthServiceReply;
import com.github.zigcat.greenhub.user_provider.events.events.RegisterMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.exceptions.AuthException;
import com.github.zigcat.greenhub.user_provider.exceptions.NotFoundException;
import com.github.zigcat.greenhub.user_provider.utils.UserUtils;
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
    public void handleRegistrationEvent(RegisterMessageQueryAdapterEvent event){
        log.info("Registration event handling initiated");
        RegisterRequest request = event.getRequest();
        CompletableFuture<RegisterAuthServiceReply> replyFuture = event.getReplyFuture();
        UserDTO userDTO = UserUtils.toDTO(request);
        log.info("Making database call...");
        service.register(Mono.just(userDTO))
                .doOnNext(user -> {
                    log.info("Database call finished successfully");
                    RegisterResponse response = UserUtils.toRegResponse(user);
                    RegisterAuthServiceReply reply = new RegisterAuthServiceReply(response);
                    log.info("Sending data to event publisher...");
                    replyFuture.complete(reply);
                })
                .doOnError(e -> {
                    log.error("Error while processing event ", e);
                    replyFuture.completeExceptionally(e);
                })
                .subscribe();
    }

    @EventListener
    public void handleAuthorizationEvent(AuthorizeMessageQueryAdapterEvent event){
        String username = event.getRequest().getUsername();
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture = event.getReplyFuture();
        service.retrieveByEmailWithScopes(username)
                .doOnNext(response -> {
                    if(response.getId() != null){
                        AuthorizeAuthServiceReply reply = new AuthorizeAuthServiceReply(response);
                        replyFuture.complete(reply);
                    } else {
                        throw new NotFoundException("User not found");
                    }
                })
                .doOnError(e -> {
                    log.error("Error while processing event ", e);
                    replyFuture.completeExceptionally(e);
                })
                .subscribe();
    }

    @EventListener
    public void handleLoginEvent(LoginMessageQueryAdapterEvent event){
        LoginRequest request = event.getRequest();
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture = event.getReplyFuture();
        checkUser(request.getUsername(), request.getPassword())
                .flatMap(user -> service.retrieveByIdWithScopes(user.getId()))
                .doOnNext(response -> {
                    if(response.getId() != null){
                        AuthorizeAuthServiceReply reply = new AuthorizeAuthServiceReply(response);
                        replyFuture.complete(reply);
                    } else {
                        throw new NotFoundException("User not found");
                    }
                })
                .doOnError(e -> {
                    log.error("Error while processing event ", e);
                    replyFuture.completeExceptionally(e);
                })
                .subscribe();
    }

    public Mono<AppUser> checkUser(String username, String password){
        return service.retrieveByEmail(username)
                .map(user -> {
                    if(BCrypt.checkpw(password, user.getPassword())){
                        return user;
                    }
                    throw new AuthException("Wrong password");
                }).switchIfEmpty(Mono.error(new NotFoundException("User not found")));
    }
}
