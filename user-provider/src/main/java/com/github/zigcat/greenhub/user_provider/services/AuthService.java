package com.github.zigcat.greenhub.user_provider.services;

import com.github.zigcat.greenhub.user_provider.dto.UserDTO;
import com.github.zigcat.greenhub.user_provider.dto.requests.UserAuthRequest;
import com.github.zigcat.greenhub.user_provider.dto.requests.UserRegisterRequest;
import com.github.zigcat.greenhub.user_provider.dto.responses.UserAuthResponse;
import com.github.zigcat.greenhub.user_provider.dto.responses.UserRegisterResponse;
import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import com.github.zigcat.greenhub.user_provider.events.AuthorizeAuthServiceReply;
import com.github.zigcat.greenhub.user_provider.events.AuthorizeMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.events.RegisterAuthServiceReply;
import com.github.zigcat.greenhub.user_provider.events.RegisterMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.user_provider.exceptions.AuthException;
import com.github.zigcat.greenhub.user_provider.exceptions.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {
    private final UserService service;

    @Autowired
    public AuthService(UserService service) {
        this.service = service;
    }

    @EventListener
    public void handleRegistrationEvent(RegisterMessageQueryAdapterEvent event){
        UserRegisterRequest request = event.getRequest();
        CompletableFuture<RegisterAuthServiceReply> replyFuture = event.getReplyFuture();
        UserDTO dto = new UserDTO(
                request.getFname(),
                request.getLname(),
                request.getEmail(),
                request.getPassword(),
                request.getRole());
        AppUser user = service.create(Mono.just(dto)).block();
        if(user == null){
            replyFuture.completeExceptionally(new ServerException("Not found"));
        } else {
            UserRegisterResponse response = new UserRegisterResponse(
                    user.getId(),
                    user.getFname(),
                    user.getLname(),
                    user.getEmail(),
                    request.getRole(),
                    user.getRegDate());
            replyFuture.complete(new RegisterAuthServiceReply(response));
        }
    }

    @EventListener
    public void handleAuthorizeEvent(AuthorizeMessageQueryAdapterEvent event){
        UserAuthRequest request = event.getRequest();
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture = event.getReplyFuture();
        UserAuthResponse response = processAuthorization(request);
        if(response == null){
            replyFuture.completeExceptionally(new AuthException("User not found"));
        } else {
            replyFuture.complete(new AuthorizeAuthServiceReply(response));
        }
    }

    private UserAuthResponse processAuthorization(UserAuthRequest request){
        String username = request.getUsername();
        AppUser user = service.retrieveByEmail(username).block();
        if(user == null){
            return null;
        }
        return new UserAuthResponse(user.getId(),
                user.getFname(),
                user.getLname(),
                user.getEmail(),
                user.getRole().toString());
    }
}
