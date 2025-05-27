package com.github.zigcat.greenhub.article_provider.config;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.InteractionModel;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
public class StartupDataInitializer {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public StartupDataInitializer(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @PostConstruct
    public void init() {
        Query query = new Query(Criteria.where("updatedAt").exists(false));
        Update update = new Update().set("updatedAt", Instant.now());

        reactiveMongoTemplate.updateMulti(query, update, InteractionModel.class)
                .subscribe(result -> log.warn("Updated Mongo docs: " + result.getModifiedCount()));
    }
}
