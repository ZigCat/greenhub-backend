package com.github.zigcat.greenhub.auth_provider.application.events;

import com.github.zigcat.greenhub.auth_provider.infrastructure.InfrastructureDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class AuthorizeEvent extends ApplicationEvent {
    private String token;
    private CompletableFuture<InfrastructureDTO.UserAuth> replyFuture;

    public AuthorizeEvent(Object source, String token, CompletableFuture<InfrastructureDTO.UserAuth> replyFuture) {
        super(source);
        this.token = token;
        this.replyFuture = replyFuture;
    }
}
