package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.domain.AppUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {
    Mono<AppUser> retrieve(Long id);
    Mono<AppUser> promote(Long id);
    Flux<AppUser> listByIds(List<Long> ids);
}
