package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import org.apache.mahout.cf.taste.model.DataModel;
import reactor.core.publisher.Mono;

public interface RecommendationRepository {
    Mono<DataModel> loadModel();
}
