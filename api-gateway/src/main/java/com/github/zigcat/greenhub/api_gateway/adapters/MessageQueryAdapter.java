package com.github.zigcat.greenhub.api_gateway.adapters;

import com.github.zigcat.greenhub.api_gateway.dto.requests.JwtRequest;
import com.github.zigcat.greenhub.api_gateway.dto.responces.UserAuthResponse;
import reactor.core.publisher.Mono;

public interface MessageQueryAdapter {
    Mono<UserAuthResponse> performAndAwait(JwtRequest data);
}
