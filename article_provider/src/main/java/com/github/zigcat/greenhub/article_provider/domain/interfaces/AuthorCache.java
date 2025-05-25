package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuthorCache {
    Mono<Map<Long, Long>> getCachedRelations();
    Mono<Void> cacheRelations(Map<Long, Long> relations);
}
