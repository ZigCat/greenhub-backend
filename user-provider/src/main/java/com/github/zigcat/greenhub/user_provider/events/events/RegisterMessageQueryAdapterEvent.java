package com.github.zigcat.greenhub.user_provider.events.events;

import com.github.zigcat.greenhub.user_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.user_provider.events.replies.RegisterAuthServiceReply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class RegisterMessageQueryAdapterEvent extends ApplicationEvent {
    private RegisterRequest request;
    private CompletableFuture<RegisterAuthServiceReply> replyFuture;

    public RegisterMessageQueryAdapterEvent(Object source,
                                            RegisterRequest request,
                                            CompletableFuture<RegisterAuthServiceReply> replyFuture) {
        super(source);
        this.request = request;
        this.replyFuture = replyFuture;
    }
}
