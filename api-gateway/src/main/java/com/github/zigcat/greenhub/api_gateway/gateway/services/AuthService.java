package com.github.zigcat.greenhub.api_gateway.gateway.services;

import com.github.zigcat.greenhub.api_gateway.dto.requests.TokenType;
import com.github.zigcat.greenhub.api_gateway.exceptions.AuthException;
import com.github.zigcat.greenhub.api_gateway.exceptions.ServerException;
import com.github.zigcat.greenhub.api_gateway.kafka.service.MessageQueryService;
import com.github.zigcat.greenhub.api_gateway.dto.responces.UserAuthResponse;
import com.github.zigcat.greenhub.api_gateway.dto.requests.JwtRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthService {
    private final MessageQueryService messageQueryService;

    @Autowired
    public AuthService(MessageQueryService messageQueryService) {
        this.messageQueryService = messageQueryService;
    }

    public Mono<UserAuthResponse> authorizeByToken(String token) throws AuthException, ServerException {
        log.info("Preparing token "+token+"to share via Kafka");
        JwtRequest request = new JwtRequest(token, TokenType.ACCESS);
        return messageQueryService.performAuthorizeRequest(request)
                .switchIfEmpty(Mono.error(new AuthException()))
                .onErrorMap(e -> new ServerException("Server error: "+e));
    }
}
