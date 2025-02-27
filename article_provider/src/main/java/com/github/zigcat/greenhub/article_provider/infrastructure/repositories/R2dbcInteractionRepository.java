package com.github.zigcat.greenhub.article_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.article_provider.domain.Interaction;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.InteractionRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc.ReactiveInteractionRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.InteractionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcInteractionRepository implements InteractionRepository {
    private final ReactiveInteractionRepository repository;

    public R2dbcInteractionRepository(ReactiveInteractionRepository repository) {
        this.repository = repository;
    }

    @Override
    public InteractionModel toModel(Interaction entity) {
        return new InteractionModel(
                entity.getId(),
                entity.getUserId(),
                entity.getArticleId(),
                entity.isLike(),
                entity.isStar(),
                entity.getViews(),
                entity.getRating()
        );
    }

    @Override
    public Interaction toEntity(InteractionModel model) {
        return new Interaction(
                model.getId(),
                model.getUserId(),
                model.getArticleId(),
                model.isLike(),
                model.isStar(),
                model.getViews(),
                model.getRating()
        );
    }

    @Override
    public Flux<InteractionModel> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<InteractionModel> save(InteractionModel model) {
        return repository.save(model);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.deleteById(id);
    }
}
