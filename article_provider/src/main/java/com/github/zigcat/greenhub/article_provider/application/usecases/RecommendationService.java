package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationCache;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericRecommendedItem;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model); // Увеличили соседей
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

            long[] neighbors = neighborhood.getUserNeighborhood(userId);
            log.info("User {} neighbors: {}", userId, Arrays.toString(neighbors));

            FastIDSet seenItems = model.getItemIDsFromUser(userId);
            LongPrimitiveIterator allItemIDs = model.getItemIDs();

            List<RecommendedItem> estimated = new ArrayList<>();
            while (allItemIDs.hasNext()) {
                long itemId = allItemIDs.nextLong();
                if (seenItems.contains(itemId)) continue;
                float pref = recommender.estimatePreference(userId, itemId);
                if (!Float.isNaN(pref)) {
                    estimated.add(new GenericRecommendedItem(itemId, pref));
                }
            }
            List<Long> recs = estimated.stream()
                    .sorted(Comparator.comparingDouble(RecommendedItem::getValue).reversed())
                    .limit(numRecommendations)
                    .map(RecommendedItem::getItemID)
                    .toList();
            log.info("Recommendations for user {}: {}", userId, recs);
            return recs;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
