package com.github.zigcat.greenhub.article_provider.infrastructure.utils;

import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleContentModel;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.ArticleModel;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;

import java.time.LocalDateTime;

public class ArticleUtils {
    public static ArticleModel toModel(Article entity){
        return new ArticleModel(
                entity.getId(),
                entity.getTitle(),
                entity.getCreationDate(),
                entity.getArticleStatus(),
                entity.getPaidStatus(),
                entity.getCreator(),
                entity.getCategory()
        );
    }

    public static ArticleContentModel toContentModel(Article entity){
        return new ArticleContentModel(
                entity.getId(),
                entity.getContent()
        );
    }

    public static Article toEntity(ArticleModel model, ArticleContentModel content){
        String contentData = null;
        if(content != null) contentData = content.getContent();
        return new Article(
                model.getId(),
                model.getTitle(),
                contentData,
                model.getCreationDate(),
                model.getArticleStatus(),
                model.getPaidStatus(),
                model.getCreator(),
                model.getCategory()
        );
    }
}
