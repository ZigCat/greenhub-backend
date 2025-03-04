package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.domain.AppUser;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<AppUser> retrieve(Long id);
}
