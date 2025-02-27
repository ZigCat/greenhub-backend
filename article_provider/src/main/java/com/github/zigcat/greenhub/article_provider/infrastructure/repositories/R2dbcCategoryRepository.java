package com.github.zigcat.greenhub.article_provider.infrastructure.repositories;

import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.CategoryRepository;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc.ReactiveCategoryRepository;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.CategoryModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class R2dbcCategoryRepository implements CategoryRepository {
    private final ReactiveCategoryRepository repository;

    public R2dbcCategoryRepository(ReactiveCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Flux<CategoryModel> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<CategoryModel> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<CategoryModel> save(CategoryModel model) {
        return repository.save(model);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.deleteById(id);
    }
}
