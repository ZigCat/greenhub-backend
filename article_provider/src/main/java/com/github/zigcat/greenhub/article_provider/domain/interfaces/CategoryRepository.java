package com.github.zigcat.greenhub.article_provider.domain.interfaces;

import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.CategoryModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryRepository {
    Flux<CategoryModel> findAll();
    Mono<CategoryModel> findById(Long id);
    Mono<CategoryModel> save(CategoryModel model);
    Mono<Void> delete(Long id);
}
