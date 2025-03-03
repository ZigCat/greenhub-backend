package com.github.zigcat.greenhub.article_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.article_provider.domain.interfaces.ArticleContentRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc.ReactiveArticleContentRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.article_provider.infrastructure.exceptions.DatabaseException;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
import io.r2dbc.spi.R2dbcException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcArticleContentRepository implements ArticleContentRepository {
    private final ReactiveArticleContentRepository repository;

    public R2dbcArticleContentRepository(ReactiveArticleContentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<ArticleContentModel> findAll() {
        return repository.findAll()
                .onErrorMap(e -> {
                    if(e instanceof R2dbcException){
                        throw new BadRequestInfrastructureException("Illegal data");
                    }
                    throw new DatabaseException("Error while accessing source");
                });
    }

    @Override
    public Mono<ArticleContentModel> findById(Long id) {
        return repository.findByArticleId(id)
                .onErrorMap(e -> {
                    if(e instanceof R2dbcException){
                        throw new BadRequestInfrastructureException("Illegal data");
                    }
                    throw new DatabaseException("Error while accessing source");
                });
    }

    @Override
    public Mono<ArticleContentModel> save(ArticleContentModel model) {
        return repository.save(model)
                .onErrorMap(e -> {
                    if(e instanceof R2dbcException){
                        throw new BadRequestInfrastructureException("Illegal data");
                    }
                    throw new DatabaseException("Error while accessing source");
                });
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.deleteByArticleId(id)
                .onErrorMap(e -> {
                    if(e instanceof R2dbcException){
                        throw new BadRequestInfrastructureException("Illegal data");
                    }
                    throw new DatabaseException("Error while accessing source");
                });
    }
}
