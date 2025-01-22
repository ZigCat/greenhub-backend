package com.github.zigcat.greenhub.auth_provider.adapters;

import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.RegisterResponse;
import reactor.core.publisher.Mono;

public interface MessageQueryAdapter {
    void processMessage();

    Mono<RegisterResponse> registerAndAwait(RegisterRequest data);
}
