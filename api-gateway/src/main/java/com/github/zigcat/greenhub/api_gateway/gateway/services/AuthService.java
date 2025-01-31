package com.github.zigcat.greenhub.api_gateway.gateway.services;

import com.github.zigcat.greenhub.api_gateway.adapters.MessageQueryAdapter;
import com.github.zigcat.greenhub.api_gateway.dto.requests.TokenType;
import com.github.zigcat.greenhub.api_gateway.exceptions.AuthException;
import com.github.zigcat.greenhub.api_gateway.exceptions.ServerException;
import com.github.zigcat.greenhub.api_gateway.dto.responces.UserAuthResponse;
import com.github.zigcat.greenhub.api_gateway.dto.requests.JwtRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthService {
    private final MessageQueryAdapter adapter;

    @Autowired
    public AuthService(MessageQueryAdapter adapter) {
        this.adapter = adapter;
    }

    public Mono<UserAuthResponse> authorizeByToken(String token) throws AuthException, ServerException {
        log.info("Preparing token "+token+"to share via Kafka");
        JwtRequest request = new JwtRequest(token, TokenType.ACCESS);
        return adapter.performAndAwait(request)
                .switchIfEmpty(Mono.error(new AuthException("Wrong token")))
                .onErrorMap(e -> new ServerException(e.getMessage()));
    }
}
