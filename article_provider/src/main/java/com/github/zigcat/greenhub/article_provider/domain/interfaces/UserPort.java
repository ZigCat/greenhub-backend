package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.domain.AppUser;
import reactor.core.publisher.Mono;

public interface UserPort {
    Mono<AppUser> retrieve(Long id);
}
