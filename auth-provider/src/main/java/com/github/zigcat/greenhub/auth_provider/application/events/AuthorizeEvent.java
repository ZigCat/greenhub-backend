package com.github.zigcat.greenhub.auth_provider.application.events;

import com.github.zigcat.greenhub.auth_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.auth_provider.infrastructure.adapter.JwtRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class AuthorizeEvent extends ApplicationEvent {
    private JwtRequest jwtRequest;
    private CompletableFuture<InfrastructureDTO.UserAuth> replyFuture;

    public AuthorizeEvent(
            Object source,
            JwtRequest jwtRequest,
            CompletableFuture<InfrastructureDTO.UserAuth> replyFuture
    ) {
        super(source);
        this.jwtRequest = jwtRequest;
        this.replyFuture = replyFuture;
    }
}
