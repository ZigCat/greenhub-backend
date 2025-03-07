package com.github.zigcat.greenhub.api_gateway.domain.interfaces;

import com.github.zigcat.greenhub.api_gateway.domain.AppUser;
import com.github.zigcat.greenhub.api_gateway.domain.schemas.TokenType;
import com.github.zigcat.greenhub.api_gateway.infrastructure.InfrastructureDTO;
import reactor.core.publisher.Mono;

public interface MessageQueryAdapter {
    Mono<AppUser> performAndAwait(String token, TokenType tokenType);
}
