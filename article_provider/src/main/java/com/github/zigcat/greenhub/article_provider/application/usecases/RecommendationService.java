package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationCache;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationCache recommendationCache;

    public RecommendationService(
            RecommendationRepository recommendationRepository,
            RecommendationCache recommendationCache
    ) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationCache = recommendationCache;
    }

    public Mono<List<Long>> getRecommendations(Long userId) {
        return recommendationCache.getCachedModel()
                .switchIfEmpty(recommendationRepository.loadModel()
                        .doOnNext(recommendationCache::cacheModel))
                .flatMap(model -> generateRecommendations(userId, 10, model));
    }

    private Mono<List<Long>> generateRecommendations(Long userId, int numRecommendations, DataModel model) {
        return Mono.fromCallable(() -> {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            long[] neighbors = neighborhood.getUserNeighborhood(userId);
            log.info("User {} neighbors: {}", userId, Arrays.toString(neighbors));
            List<Long> recs = recommender.recommend(userId, numRecommendations)
                    .stream()
                    .map(RecommendedItem::getItemID)
                    .toList();
            log.info("Recommendations for user {}: {}", userId, recs);
            return recs;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
