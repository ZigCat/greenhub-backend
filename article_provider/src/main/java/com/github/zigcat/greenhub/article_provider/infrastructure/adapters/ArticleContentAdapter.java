package com.github.zigcat.greenhub.article_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleContentRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
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
        Query query = new Query(Criteria.where("articleId").is(id));
        return reactiveMongoTemplate.findOne(query, ArticleContentModel.class);
    }

    @Override
    public Mono<ArticleContentModel> save(ArticleContentModel model) {
        return reactiveMongoTemplate.save(model);
    }

    @Override
    public Mono<Void> delete(Long id) {
        Query query = new Query(Criteria.where("articleId").is(id));
        return reactiveMongoTemplate.remove(query, ArticleContentModel.class).then();
    }
}
