package com.github.zigcat.greenhub.article_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleContentRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.ConflictInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
import com.mongodb.DuplicateKeyException;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ArticleContentAdapter implements ArticleContentRepository {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public ArticleContentAdapter(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Mono<ArticleContentModel> findById(Long id) {
        if (id == null) {
            return Mono.error(new BadRequestInfrastructureException("ID cannot be null"));
        }
        Query query = new Query(Criteria.where("articleId").is(id));
        return reactiveMongoTemplate.findOne(query, ArticleContentModel.class)
                .onErrorMap(e -> new DatabaseException("Article service unavailable"));
    }

    @Override
    public Mono<ArticleContentModel> save(ArticleContentModel model) {
        if (model == null) {
            return Mono.error(new BadRequestInfrastructureException("Entity cannot be null"));
        }
        return reactiveMongoTemplate.save(model)
                .onErrorMap(e -> {
                    if(e instanceof DuplicateKeyException){
                        throw new ConflictInfrastructureException("Data conflict occurred while trying to transact");
                    }
                    throw new DatabaseException("Article service unavailable");
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        if(id == null) {
            return Mono.error(new BadRequestInfrastructureException("ID cannot be null"));
        }
        Query query = new Query(Criteria.where("articleId").is(id));
        return reactiveMongoTemplate.remove(query, ArticleContentModel.class)
                .onErrorMap(e -> new DatabaseException("Article service unavailable"))
                .then();
    }
}
