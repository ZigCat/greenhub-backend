package com.github.zigcat.greenhub.api_gateway.application.usecases;

import com.github.zigcat.greenhub.api_gateway.domain.AppUser;
import com.github.zigcat.greenhub.api_gateway.domain.interfaces.MessageQueryAdapter;
import com.github.zigcat.greenhub.api_gateway.domain.schemas.TokenType;
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

    public Mono<AppUser> authorizeByToken(String token) {
        log.info("Preparing token {} to share via Kafka", token);
        return adapter.performAndAwait(token, TokenType.ACCESS);
    }
}
