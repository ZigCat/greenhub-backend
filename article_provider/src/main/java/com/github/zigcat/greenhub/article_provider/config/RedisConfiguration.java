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
    public ReactiveRedisTemplate<String, DataModel> dataModelRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<DataModel> valueSerializer = new Jackson2JsonRedisSerializer<>(DataModel.class);
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        RedisSerializationContext<String, DataModel> context =
                RedisSerializationContext
                        .<String, DataModel>newSerializationContext(keySerializer)
                        .value(valueSerializer)
                        .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
