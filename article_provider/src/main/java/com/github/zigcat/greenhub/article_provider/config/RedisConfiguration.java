package com.github.zigcat.greenhub.article_provider.config;

import org.apache.mahout.cf.taste.model.DataModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {
    @Bean
    public ReactiveRedisTemplate<String, String> dataModelRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<String> valueSerializer = new Jackson2JsonRedisSerializer<>(String.class);
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        RedisSerializationContext<String, String> context =
                RedisSerializationContext
                        .<String, String>newSerializationContext(keySerializer)
                        .value(valueSerializer)
                        .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
