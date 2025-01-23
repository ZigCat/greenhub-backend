package com.github.zigcat.greenhub.user_provider.events.events;

import com.github.zigcat.greenhub.user_provider.dto.mq.requests.LoginRequest;
import com.github.zigcat.greenhub.user_provider.events.replies.AuthorizeAuthServiceReply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class LoginMessageQueryAdapterEvent extends ApplicationEvent {
    private LoginRequest request;
    private CompletableFuture<AuthorizeAuthServiceReply> replyFuture;

    public LoginMessageQueryAdapterEvent(Object source, LoginRequest request, CompletableFuture<AuthorizeAuthServiceReply> replyFuture) {
        super(source);
        this.request = request;
        this.replyFuture = replyFuture;
    }
}
