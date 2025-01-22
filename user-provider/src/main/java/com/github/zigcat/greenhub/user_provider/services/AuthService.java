package com.github.zigcat.greenhub.user_provider.services;

import com.github.zigcat.greenhub.user_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.user_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.user_provider.dto.rest.entities.UserDTO;
import com.github.zigcat.greenhub.user_provider.events.RegisterAuthServiceReply;
import com.github.zigcat.greenhub.user_provider.events.RegisterMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
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
        service.create(Mono.just(userDTO))
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
}
