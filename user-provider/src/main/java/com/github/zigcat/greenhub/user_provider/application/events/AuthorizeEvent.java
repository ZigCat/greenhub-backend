package com.github.zigcat.greenhub.user_provider.application.events;

import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class AuthorizeEvent extends ApplicationEvent {
    private String request;
    private CompletableFuture<AppUser> replyFuture;

    public AuthorizeEvent(
            Object source,
            String request,
            CompletableFuture<AppUser> replyFuture
    ) {
        super(source);
        this.request = request;
        this.replyFuture = replyFuture;
    }
}
