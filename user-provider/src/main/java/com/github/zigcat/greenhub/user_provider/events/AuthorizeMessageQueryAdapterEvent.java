package com.github.zigcat.greenhub.user_provider.events;

import com.github.zigcat.greenhub.user_provider.dto.requests.UserAuthRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class AuthorizeMessageQueryAdapterEvent extends ApplicationEvent {
    private UserAuthRequest request;
    private CompletableFuture<AuthorizeAuthServiceReply> replyFuture;

    public AuthorizeMessageQueryAdapterEvent(Object source,
                                             UserAuthRequest request,
                                             CompletableFuture<AuthorizeAuthServiceReply> replyFuture) {
        super(source);
        this.request = request;
        this.replyFuture = replyFuture;
    }
}
