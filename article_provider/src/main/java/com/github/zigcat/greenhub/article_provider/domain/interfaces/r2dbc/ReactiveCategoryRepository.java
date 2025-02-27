package com.github.zigcat.greenhub.article_provider.domain.interfaces.r2dbc;

import com.github.zigcat.greenhub.article_provider.infrastructure.models.CategoryModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ReactiveCategoryRepository extends ReactiveCrudRepository<CategoryModel, Long> {
}
