package com.github.zigcat.greenhub.article_provider.infrastructure.utils;

import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.CategoryModel;
import org.springframework.stereotype.Component;

public class CategoryUtils {
    public static CategoryModel toModel(Category entity) {
        return new CategoryModel(
                entity.getId(),
                entity.getName()
        );
    }

    public static Category toEntity(CategoryModel model) {
        return new Category(
                model.getId(),
                model.getName()
        );
    }
}
