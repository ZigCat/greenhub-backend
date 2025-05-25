package com.github.zigcat.greenhub.article_provider.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.AuthorCache;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.DatabaseException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
public class RedisAuthorCache implements AuthorCache {
    private static final String CACHE_KEY = "articles:author";
    private static final Duration TTL = Duration.ofMinutes(5);
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisAuthorCache(@Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<Map<Long, Long>> getCachedRelations() {
        return redisTemplate.opsForValue()
                .get(CACHE_KEY)
                .flatMap(json -> {
                    try {
                        return objectMapper.readValue(json, new TypeReference<>() {});
                    } catch (JsonProcessingException e) {
                        return Mono.error(new DatabaseException(e.getMessage()));
                    }
                });
    }

    @Override
    public Mono<Void> cacheRelations(Map<Long, Long> relations) {
        try {
            String json = objectMapper.writeValueAsString(relations);
            return redisTemplate.opsForValue().set(CACHE_KEY, json, TTL).then();
        } catch (JsonProcessingException e) {
            return Mono.error(new DatabaseException(e.getMessage()));
        }
    }
}
