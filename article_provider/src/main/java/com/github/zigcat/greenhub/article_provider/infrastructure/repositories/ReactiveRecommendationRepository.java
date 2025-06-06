package com.github.zigcat.greenhub.article_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import com.github.zigcat.greenhub.article_provider.infrastructure.projections.InteractionProjection;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.InteractionRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationRepository;
import com.github.zigcat.greenhub.article_provider.utils.InteractionUtils;
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
    private final InteractionRepository interactions;
    private final ArticleRepository articles;

    public ReactiveRecommendationRepository(InteractionRepository interactions, ArticleRepository articles) {
        this.interactions = interactions;
        this.articles = articles;
    }

    @Override
    public Mono<DataModel> loadModel() {
        return interactions.findAll()
                .filter(interaction -> interaction.getUserId() != null && interaction.getArticleId() != null)
                .map(InteractionUtils::toProjection)
                .distinct()
                .collectList()
                .map(this::convertToDataModel);
    }

    @Override
    public Mono<Map<Long, Long>> loadRelations() {
        return articles.findAll().collectMap(ArticleModel::getId, ArticleModel::getCreator);
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
