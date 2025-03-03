package com.github.zigcat.greenhub.article_provider.presentation;

import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;

import java.time.LocalDateTime;

public class DTO {
    public record ArticleCreateDTO(
            String title,
            String content,
            Long category
    ){
        public boolean isMissing(){
            return title == null || content == null || category == null;
        }
    }

    public record ArticleGetDTO(
            Long id,
            String title,
            Long category,
            String status,
            UserArticleDTO user,
            String content
    ){}

    public record ArticleModerateDTO(
            String status
    ){}

    public record UserArticleDTO(
            Long id,
            String fname,
            String lname,
            String role
    ){}

    public record ApiError(
            String message,
            int status
    ){}
}
