package com.github.zigcat.greenhub.article_provider.infrastructure.cache;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationCache;
import org.apache.mahout.cf.taste.model.DataModel;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RedisRecommendationCache implements RecommendationCache {
    private final ReactiveRedisTemplate<String, DataModel> redisTemplate;
    private static final String CACHE_KEY = "recommendation:dataModel";
    private static final Duration TTL = Duration.ofSeconds(30);

    public RedisRecommendationCache(ReactiveRedisTemplate<String, DataModel> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<DataModel> getCachedModel() {
        return redisTemplate.opsForValue().get(CACHE_KEY);
    }

    @Override
    public void cacheModel(DataModel dataModel) {
        redisTemplate.opsForValue().set(CACHE_KEY, dataModel, TTL).subscribe();
    }
}
