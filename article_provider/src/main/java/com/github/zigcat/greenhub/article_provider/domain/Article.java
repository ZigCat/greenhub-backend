package com.github.zigcat.greenhub.article_provider.domain;

import com.github.zigcat.greenhub.article_provider.domain.schemas.ArticleStatus;
import com.github.zigcat.greenhub.article_provider.domain.schemas.PaidStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Article domain model")
public class Article {
    @Schema(example = "1")
    private Long id;
    @Schema(example = "Sample")
    private String title;
    @Schema(example = "Sample content")
    private String content;
    @Schema(example = "2025-03-17T13:53:33.149282")
    private LocalDateTime creationDate;
    @Schema(example = "MODERATION")
    private ArticleStatus articleStatus;
    @Schema(example = "FREE")
    private PaidStatus paidStatus;
    @Schema
    private AppUser creator;
    @Schema
    private Category category;
    @Schema
    private Interaction interaction;

    public Article(String title, String content, Category category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
