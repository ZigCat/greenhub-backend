package com.github.zigcat.greenhub.auth_provider.adapters;

import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.AuthorizeRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.LoginRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.UserAuthResponse;
import reactor.core.publisher.Mono;

public interface MessageQueryAdapter {
    void processMessage();
    Mono<RegisterResponse> registerAndAwait(RegisterRequest data);
    Mono<UserAuthResponse> authorizeAndAwait(AuthorizeRequest data);
    Mono<UserAuthResponse> loginAndAwait(LoginRequest data);
}
