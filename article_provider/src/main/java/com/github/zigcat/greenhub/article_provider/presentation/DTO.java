package com.github.zigcat.greenhub.article_provider.presentation;

import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class DTO {
    @Schema(description = "Article data transfer object")
    public record ArticleDTO(
            @Schema(example = "Sample")
            String title,
            @Schema(example = "Sample content")
            String content,
            @Schema(example = "Sample article")
            String annotation,
            @Schema(example = "1")
            Long category
    ){}

    @Schema(description = "Category data transfer object")
    public record CategoryDTO(
            @Schema(example = "Ecology")
            String name,
            @Schema(example = "Simple description for category")
            String description
    ){}

    @Schema(description = "API error response")
    public record ApiError(
            @Schema(example = "Internal server error")
            String message,
            @Schema(example = "500")
            int status
    ){}
}
