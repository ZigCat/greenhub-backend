package com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.InteractionModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReactiveInteractionRepository extends ReactiveMongoRepository<InteractionModel, String> {
}
