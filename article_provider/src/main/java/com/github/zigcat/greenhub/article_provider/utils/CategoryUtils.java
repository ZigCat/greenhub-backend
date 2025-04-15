package com.github.zigcat.greenhub.article_provider.utils;

import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.CategoryModel;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import org.springframework.stereotype.Component;

public class CategoryUtils {
    public static CategoryModel toModel(Category entity) {
        return new CategoryModel(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public static Category toEntity(CategoryModel model) {
        return new Category(
                model.getId(),
                model.getName(),
                model.getDescription()
        );
    }

    public static Category toEntity(DTO.CategoryDTO dto){
        return new Category(dto.name(), dto.description());
    }
}
