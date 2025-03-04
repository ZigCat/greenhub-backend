package com.github.zigcat.greenhub.article_provider.presentation;

import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;

import java.time.LocalDateTime;

public class DTO {
    public record ArticleDTO(
            String title,
            String content,
            Long category
    ){}

    public record CategoryDTO(
            String name
    ){}

    public record ApiError(
            String message,
            int status
    ){}
}
