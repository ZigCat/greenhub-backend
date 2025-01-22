package com.github.zigcat.greenhub.auth_provider.events.events;

import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.JwtRequest;
import com.github.zigcat.greenhub.auth_provider.events.replies.AuthorizeAuthServiceReply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class AuthorizeMessageQueryAdapterEvent extends ApplicationEvent {
    private JwtRequest jwtRequest;
    private CompletableFuture<AuthorizeAuthServiceReply> replyFuture;

    public AuthorizeMessageQueryAdapterEvent(
            Object source,
            JwtRequest jwtRequest,
            CompletableFuture<AuthorizeAuthServiceReply> replyFuture
    ) {
        super(source);
        this.jwtRequest = jwtRequest;
        this.replyFuture = replyFuture;
    }
}
