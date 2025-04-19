package com.github.zigcat.greenhub.article_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.InteractionRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.ConflictInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.NotFoundInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.InteractionModel;
import com.mongodb.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class InteractionAdapter implements InteractionRepository {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public InteractionAdapter(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Flux<InteractionModel> findAll() {
        return reactiveMongoTemplate.findAll(InteractionModel.class)
                .onErrorMap(e -> new DatabaseException("Article service unavailable"));
    }

    @Override
    public Flux<InteractionModel> findByArticleId(Long articleId) {
        if(articleId == null) {
            return Flux.error(new BadRequestInfrastructureException("ID cannot be null"));
        }
        Query query = new Query(Criteria.where("articleId").is(articleId));
        return reactiveMongoTemplate.find(query, InteractionModel.class)
                .onErrorMap(e -> new DatabaseException("Article service unavailable"));
    }

    @Override
    public Mono<InteractionModel> findByUserAndArticle(Long userId, Long articleId) {
        Query query = new Query(Criteria.where("userId").is(userId).and("articleId").is(articleId));
        return reactiveMongoTemplate.findOne(query, InteractionModel.class)
                .switchIfEmpty(Mono.error(new NotFoundInfrastructureException("Interaction not found")))
                .onErrorMap(e -> {
                    if(e instanceof NotFoundInfrastructureException) return e;
                    return new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Mono<InteractionModel> upsert(Long userId, Long articleId, Integer like, Integer views, Double rating) {
        Query query = new Query(Criteria.where("userId").is(userId).and("articleId").is(articleId));
        Update update = new Update();
        if(like != null) update.set("like", like > 0);
        if(views != null && views != 0) update.inc("views");
        if(rating != null) update.set("rating", rating);
        return reactiveMongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                InteractionModel.class
        ).onErrorMap(e -> {
                    if(e instanceof DuplicateKeyException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }
}
