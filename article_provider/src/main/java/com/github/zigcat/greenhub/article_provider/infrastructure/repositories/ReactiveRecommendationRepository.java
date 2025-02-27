package com.github.zigcat.greenhub.article_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.article_provider.domain.InteractionProjection;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.InteractionRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.utils.InteractionUtils;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ReactiveRecommendationRepository implements RecommendationRepository {
    private final InteractionRepository repository;

    public ReactiveRecommendationRepository(InteractionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<DataModel> loadModel() {
        return repository.findAll()
                .filter(interaction -> interaction.getUserId() != null && interaction.getArticleId() != null)
                .map(InteractionUtils::toEntity)
                .map(InteractionUtils::toProjection)
                .distinct()
                .collectList()
                .map(this::convertToDataModel);
    }

    private DataModel convertToDataModel(List<InteractionProjection> interactions) {
        FastByIDMap<PreferenceArray> data = new FastByIDMap<>();
        Map<Long, List<InteractionProjection>> userRatings = interactions.stream()
                .collect(Collectors.groupingBy(InteractionProjection::getUserId));

        userRatings.forEach((userId, userRatingList) -> {
            PreferenceArray prefs = new GenericUserPreferenceArray(userRatingList.size());
            for (int i = 0; i < userRatingList.size(); i++) {
                prefs.setUserID(i, userId);
                prefs.setItemID(i, userRatingList.get(i).getArticleId());
                prefs.setValue(i, userRatingList.get(i).getScore());
            }
            data.put(userId, prefs);
        });
        return new GenericDataModel(data);
    }
}
