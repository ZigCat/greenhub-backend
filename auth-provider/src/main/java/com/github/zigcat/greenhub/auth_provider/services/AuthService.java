package com.github.zigcat.greenhub.auth_provider.services;

import com.github.zigcat.greenhub.auth_provider.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.JwtRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.AuthorizeRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.LoginRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.UserAuthResponse;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.auth_provider.dto.rest.JwtResponse;
import com.github.zigcat.greenhub.auth_provider.events.events.AuthorizeMessageQueryAdapterEvent;
import com.github.zigcat.greenhub.auth_provider.events.replies.AuthorizeAuthServiceReply;
import com.github.zigcat.greenhub.auth_provider.exceptions.JwtAuthException;
import com.github.zigcat.greenhub.auth_provider.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AuthService {
    private final MessageQueryAdapter messageQueryAdapter;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthService(MessageQueryAdapter messageQueryAdapter, JwtProvider jwtProvider) {
        this.messageQueryAdapter = messageQueryAdapter;
        this.jwtProvider = jwtProvider;
    }

    @EventListener
    public void handleAuthorizeEvent(AuthorizeMessageQueryAdapterEvent event){
        JwtRequest request = event.getJwtRequest();
        CompletableFuture<AuthorizeAuthServiceReply> replyFuture =
                event.getReplyFuture();
        AuthorizeAuthServiceReply reply = new AuthorizeAuthServiceReply();
        processAuthorization(request)
                .doOnNext(reply::setUserResponse)
                .doOnSuccess(response -> replyFuture.complete(reply))
                .doOnError(replyFuture::completeExceptionally)
                .subscribe();
    }

    private Mono<UserAuthResponse> processAuthorization(JwtRequest request) throws JwtAuthException{
        String token = request.getToken();
        jwtProvider.validateAccessToken(token);
        String username = jwtProvider.getAccessSubject(token);
        return messageQueryAdapter.authorizeAndAwait(new AuthorizeRequest(username));
    }

    public Mono<RegisterResponse> register(RegisterRequest dto){
        return messageQueryAdapter.registerAndAwait(dto);
    }

    public Mono<JwtResponse> login(String username, String password){
        return messageQueryAdapter.loginAndAwait(new LoginRequest(username, password))
                .map(res -> new JwtResponse(jwtProvider.generateAccessToken(res), jwtProvider.generateRefreshToken(res)));
    }
}
