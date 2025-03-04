package com.github.zigcat.greenhub.article_provider.utils;

import com.github.zigcat.greenhub.article_provider.domain.AppUser;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.domain.Interaction;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;

public class ArticleUtils {
    public static ArticleModel toModel(Article entity){
        return new ArticleModel(
                entity.getId(),
                entity.getTitle(),
                entity.getCreationDate(),
                entity.getArticleStatus(),
                entity.getPaidStatus(),
                entity.getCreator().getId(),
                entity.getCategory().getId()
        );
    }

    public static ArticleContentModel toContentModel(Article entity){
        return new ArticleContentModel(
                entity.getId(),
                entity.getContent()
        );
    }

    public static Article toEntity(
            ArticleModel model,
            ArticleContentModel content,
            AppUser creator,
            Category category,
            Interaction interaction
    ){
        String contentData = null;
        if(content != null) contentData = content.getContent();
        return new Article(
                model.getId(),
                model.getTitle(),
                contentData,
                model.getCreationDate(),
                model.getArticleStatus(),
                model.getPaidStatus(),
                creator,
                category,
                interaction
        );
    }

    public static Article toEntity(DTO.ArticleDTO dto){
        return new Article(dto.title(), dto.content(), new Category(dto.category()));
    }
}
