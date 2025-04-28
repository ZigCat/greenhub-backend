package com.github.zigcat.greenhub.user_provider.domain.interfaces;

import reactor.core.publisher.Mono;

public interface AuthRepository {
    Mono<Void> erase(String username);
}
