package com.github.zigcat.greenhub.article_provider.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleCache;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.DatabaseException;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
public class RedisArticleCache implements ArticleCache {
    private static final String CACHE_KEY = "articles:all";
    private static final Duration TTL = Duration.ofMinutes(5);
    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisArticleCache(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Flux<Article> getCachedArticles() {
        return redisTemplate.opsForValue().get(CACHE_KEY)
                .flatMapMany(json -> {
                    try {
                        List<Article> list = objectMapper.readValue(json,
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Article.class));
                        return Flux.fromIterable(list);
                    } catch (JsonProcessingException e) {
                        return Flux.error(new DatabaseException("Error deserializing articles: " + e.getMessage()));
                    }
                });
    }

    @Override
    public Mono<Void> cacheArticles(List<Article> articles) {
        try {
            String json = objectMapper.writeValueAsString(articles);
            return redisTemplate.opsForValue().set(CACHE_KEY, json, TTL).then();
        } catch (JsonProcessingException e) {
            return Mono.error(new DatabaseException("Error serializing articles: " + e.getMessage()));
        }
    }
}
