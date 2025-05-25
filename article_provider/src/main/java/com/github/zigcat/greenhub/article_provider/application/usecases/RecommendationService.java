package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.AuthorCache;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationCache;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationRepository;
import lombok.extern.slf4j.Slf4j;
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

import java.util.*;

@Service
@Slf4j
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationCache recommendationCache;
    private final AuthorCache authorCache;

    public RecommendationService(
            RecommendationRepository recommendationRepository,
            RecommendationCache recommendationCache,
            AuthorCache authorCache) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationCache = recommendationCache;
        this.authorCache = authorCache;
    }

    public Mono<List<Long>> getRecommendations(Long userId) {
        return recommendationCache.getCachedModel()
                .switchIfEmpty(recommendationRepository.loadModel()
                        .doOnNext(recommendationCache::cacheModel))
                .flatMap(model -> generateRecommendations(userId, model));
    }

    public Mono<List<Long>> getRecommendationsByAuthor(Long userId, Long authorId){
        return Mono.zip(
                        getRecommendations(userId),
                        authorCache.getCachedRelations()
                                .switchIfEmpty(recommendationRepository.loadRelations()
                                        .doOnNext(authorCache::cacheRelations))
                )
                .map(tuple -> {
                    List<Long> allRecommendations = tuple.getT1();
                    Map<Long, Long> articleAuthorMap = tuple.getT2();
                    return allRecommendations.stream()
                            .filter(articleId -> authorId.equals(articleAuthorMap.get(articleId)))
                            .toList();
                });
    }

    private Mono<List<Long>> generateRecommendations(Long userId, DataModel model) {
        return Mono.fromCallable(() -> {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, model);
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
                    .map(RecommendedItem::getItemID)
                    .toList();
            log.info("Recommendations for user {}: {}", userId, recs);
            return recs;
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
