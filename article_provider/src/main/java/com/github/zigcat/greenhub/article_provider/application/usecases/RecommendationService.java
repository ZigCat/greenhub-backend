package com.github.zigcat.greenhub.article_provider.application.usecases;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc.ReactiveArticleRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc.ReactiveInteractionRepository;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
//    private final ReactiveInteractionRepository interactionRepository;
//    private final ReactiveArticleRepository articleRepository;
//    private final ReactiveRedisTemplate<String, DataModel> dataModelRedisTemplate;
//    private final String DATA_MODEL_REDIS_KEY = "dataModel";
//    private final Duration TTL = Duration.ofSeconds(30);
//
//    @Autowired
//    public RecommendationService(ReactiveInteractionRepository interactionRepository, ReactiveArticleRepository articleRepository, ReactiveRedisTemplate<String, DataModel> dataModelRedisTemplate) {
//        this.interactionRepository = interactionRepository;
//        this.articleRepository = articleRepository;
//        this.dataModelRedisTemplate = dataModelRedisTemplate;
//    }
//
//    public Flux<ArticleModel> getRecommendations(Long userId, int numRecommendations){
//        return getDataModel().flatMapMany(model -> {
//            try {
//                UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
//                UserNeighborhood neighborhood = new NearestNUserNeighborhood(5, similarity, model);
//                UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
//                List<Long> articleIds = recommender.recommend(userId, numRecommendations)
//                        .stream()
//                        .map(RecommendedItem::getItemID)
//                        .toList();
//                return articleRepository.findAllById(articleIds);
//            } catch (Exception e) {
//                return Flux.error(new RuntimeException("Ошибка генерации рекомендаций", e));
//            }
//        });
//    }
//
//    private Mono<DataModel> getDataModel(){
//        return dataModelRedisTemplate.opsForValue().get(DATA_MODEL_REDIS_KEY)
//                .switchIfEmpty(loadModel())
//                .doOnNext(this::cacheModel);
//    }
//
//    private Mono<DataModel> loadModel(){
//        return interactionRepository.findAll()
//                .filter(interaction -> interaction.getUserId() != null && interaction.getArticleId() != null)
//                .distinct()
//                .collectList()
//                .map(interactions -> {
//                    FastByIDMap<PreferenceArray> data = new FastByIDMap<>();
//                    Map<Long, List<Request.InteractionProjection>> userRatings = interactions
//                           .stream().collect(Collectors.groupingBy())
//                    userRatings.forEach((userId, userRatingList) -> {
//                        PreferenceArray prefs = new GenericUserPreferenceArray(userRatingList.size());
//                        for (int i = 0; i < userRatingList.size(); i++) {
//                            prefs.setUserID(i, userId);
//                            prefs.setItemID(i, userRatingList.get(i).getArticleId());
//                            prefs.setValue(i, userRatingList.get(i).getRecommendScore());
//                        }
//                        data.put(userId, prefs);
//                    });
//                    return new GenericDataModel(data);
//                });
//    }
//
//    private void cacheModel(DataModel dataModel){
//        dataModelRedisTemplate
//                .opsForValue()
//                .set(DATA_MODEL_REDIS_KEY, dataModel, TTL)
//                .subscribe();
//    }
}
