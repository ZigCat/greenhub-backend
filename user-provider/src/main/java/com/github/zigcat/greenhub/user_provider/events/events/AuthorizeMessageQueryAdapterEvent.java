package com.github.zigcat.greenhub.user_provider.events.events;

import com.github.zigcat.greenhub.user_provider.dto.mq.requests.AuthorizeRequest;
import com.github.zigcat.greenhub.user_provider.events.replies.AuthorizeAuthServiceReply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class AuthorizeMessageQueryAdapterEvent extends ApplicationEvent {
    private AuthorizeRequest request;
    private CompletableFuture<AuthorizeAuthServiceReply> replyFuture;

    public AuthorizeMessageQueryAdapterEvent(Object source,
                                             AuthorizeRequest request,
                                             CompletableFuture<AuthorizeAuthServiceReply> replyFuture) {
        super(source);
        this.request = request;
        this.replyFuture = replyFuture;
    }
}
