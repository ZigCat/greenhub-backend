package com.github.zigcat.greenhub.user_provider.events;

import com.github.zigcat.greenhub.user_provider.dto.requests.UserRegisterRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class RegisterMessageQueryAdapterEvent extends ApplicationEvent {
    private UserRegisterRequest request;
    private CompletableFuture<RegisterAuthServiceReply> replyFuture;

    public RegisterMessageQueryAdapterEvent(Object source,
                                            UserRegisterRequest request,
                                            CompletableFuture<RegisterAuthServiceReply> replyFuture) {
        super(source);
        this.request = request;
        this.replyFuture = replyFuture;
    }
}
