package com.github.zigcat.greenhub.auth_provider.domain.interfaces;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import reactor.core.publisher.Mono;

public interface MessageQueryAdapter {
    void processMessage();
    Mono<AppUser> registerAndAwait(AppUser data);
    Mono<AppUser> authorizeAndAwait(String data);
    Mono<AppUser> loginAndAwait(AppUser data);
}
