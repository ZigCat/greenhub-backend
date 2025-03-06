package com.github.zigcat.greenhub.user_provider.application.events;

import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.infrastructure.InfrastructureDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.concurrent.CompletableFuture;

@Getter
public class LoginEvent extends ApplicationEvent {
    private InfrastructureDTO.UserLogin request;
    private CompletableFuture<AppUser> replyFuture;

    public LoginEvent(
            Object source,
            InfrastructureDTO.UserLogin request,
            CompletableFuture<AppUser> replyFuture
    ) {
        super(source);
        this.request = request;
        this.replyFuture = replyFuture;
    }
}
