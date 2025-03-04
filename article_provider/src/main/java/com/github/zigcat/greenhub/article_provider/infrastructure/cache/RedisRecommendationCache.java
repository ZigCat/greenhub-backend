package com.github.zigcat.greenhub.article_provider.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationCache;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.DatabaseException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RedisRecommendationCache implements RecommendationCache {
    private final ReactiveRedisTemplate<String, String> dataModelRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String CACHE_KEY = "recommendation:dataModel";
    private static final Duration TTL = Duration.ofSeconds(30);

    public RedisRecommendationCache(ReactiveRedisTemplate<String, String> dataModelRedisTemplate) {
        this.dataModelRedisTemplate = dataModelRedisTemplate;
    }

    @Override
    public Mono<DataModel> getCachedModel() {
        return dataModelRedisTemplate.opsForValue()
                .get(CACHE_KEY)
                .flatMap(json -> {
                    try {
                        Map<Long, List<PreferenceDTO>> preferences = objectMapper.readValue(json, new TypeReference<>() {});
                        return Mono.just(convertToDataModel(preferences));
                    } catch (JsonProcessingException e) {
                        return Mono.error(new DatabaseException(e.getMessage()));
                    }
                });
    }

    @Override
    public void cacheModel(DataModel dataModel) {
        try {
            Map<Long, List<PreferenceDTO>> preferences = convertToMap(dataModel);
            String json = objectMapper.writeValueAsString(preferences);
            dataModelRedisTemplate.opsForValue().set(CACHE_KEY, json, TTL).subscribe();
        } catch (JsonProcessingException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private DataModel convertToDataModel(Map<Long, List<PreferenceDTO>> preferences) {
        FastByIDMap<PreferenceArray> data = new FastByIDMap<>();
        preferences.forEach((userId, prefsList) -> {
            PreferenceArray prefs = new GenericUserPreferenceArray(prefsList.size());
            for (int i = 0; i < prefsList.size(); i++) {
                prefs.setUserID(i, userId);
                prefs.setItemID(i, prefsList.get(i).articleId());
                prefs.setValue(i, prefsList.get(i).score());
            }
            data.put(userId, prefs);
        });
        return new GenericDataModel(data);
    }

    private Map<Long, List<PreferenceDTO>> convertToMap(DataModel dataModel) {
        FastByIDMap<PreferenceArray> userData = ((GenericDataModel) dataModel).getRawUserData();
        return userData.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            PreferenceArray prefs = e.getValue();
                            List<PreferenceDTO> preferences = new ArrayList<>();
                            for (int i = 0; i < prefs.length(); i++) {
                                preferences.add(new PreferenceDTO(
                                        prefs.getItemID(i),
                                        prefs.getValue(i)
                                ));
                            }
                            return preferences;
                        }
                ));
    }

    private record PreferenceDTO(Long articleId, float score) {}
}
