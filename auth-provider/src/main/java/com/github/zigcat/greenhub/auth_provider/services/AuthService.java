package com.github.zigcat.greenhub.auth_provider.services;

import com.github.zigcat.greenhub.auth_provider.dto.requests.JwtRequest;
import com.github.zigcat.greenhub.auth_provider.dto.requests.UserAuthRequest;
import com.github.zigcat.greenhub.auth_provider.dto.requests.UserRegisterRequest;
import com.github.zigcat.greenhub.auth_provider.dto.responses.UserAuthResponse;
import com.github.zigcat.greenhub.auth_provider.dto.responses.UserRegisterResponse;
import com.github.zigcat.greenhub.auth_provider.events.events.AuthorizeMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.auth_provider.events.replies.AuthorizeAuthServiceReply;
import com.github.zigcat.greenhub.auth_provider.exceptions.JwtAuthException;
import com.github.zigcat.greenhub.auth_provider.kafka.service.MessageQueryService;
import com.github.zigcat.greenhub.auth_provider.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {
    private final MessageQueryService messageQueryService;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthService(MessageQueryService messageQueryService,
                       JwtProvider jwtProvider) {
        this.messageQueryService = messageQueryService;
        this.jwtProvider = jwtProvider;
    }

    public UserRegisterResponse processRegistration(UserRegisterRequest request){
        return messageQueryService.performRegisterRequest(request);
    }

    @EventListener
    public void handleAuthorizeEvent(AuthorizeMessageQueryAdapterEvent event){
        JwtRequest request = event.getJwtRequest();
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture =
                event.getReplyFuture();
        try{
            UserAuthResponse userResponse = processAuthorization(request);
            AuthorizeAuthServiceReply reply = new AuthorizeAuthServiceReply(userResponse);
            replyFuture.complete(reply);
        } catch(JwtAuthException e) {
            replyFuture.completeExceptionally(e);
        }
    }

    private UserAuthResponse processAuthorization(JwtRequest request) throws JwtAuthException{
        String token = request.getToken();
        jwtProvider.validateAccessToken(token);
        String username = jwtProvider.getAccessSubject(token);
        return messageQueryService.performAuthorizeRequest(
                new UserAuthRequest(username));
    }
}
