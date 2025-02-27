package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.application.exceptions.ServerErrorAppException;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.AuthorizationData;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationCache;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.RecommendationRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import com.github.zigcat.greenhub.article_provider.infrastructure.utils.ArticleUtils;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final RecommendationCache recommendationCache;
    private final ArticleRepository repository;
    private final PermissionService permissions;

    public RecommendationService(
            RecommendationRepository recommendationRepository,
            RecommendationCache recommendationCache,
            ArticleRepository repository,
            PermissionService permissions
    ) {
        this.recommendationRepository = recommendationRepository;
        this.recommendationCache = recommendationCache;
        this.repository = repository;
        this.permissions = permissions;
    }

    public Flux<Article> getRecommendations(
            ServerHttpRequest request
    ) {
        AuthorizationData auth = permissions.extractAuthData(request);
        return recommendationCache.getCachedModel()
                .switchIfEmpty(recommendationRepository.loadModel()
                        .doOnNext(recommendationCache::cacheModel))
                .flatMapMany(model -> generateRecommendations(auth.getId(), 10, model))
                .map(model -> ArticleUtils.toEntity(model, null));
    }

    private Flux<ArticleModel> generateRecommendations(Long userId, int numRecommendations, DataModel model) {
        try {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            List<Long> articleIds = recommender.recommend(userId, numRecommendations)
                    .stream()
                    .map(RecommendedItem::getItemID)
                    .toList();
            return repository.findAllById(articleIds);
        } catch (Exception e) {
            return Flux.error(new ServerErrorAppException("Error while getting recommendations"));
        }
    }
}
