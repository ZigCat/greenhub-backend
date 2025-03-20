package com.github.zigcat.greenhub.article_provider.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Interaction domain model")
public class Interaction {
    @Schema(example = "1")
    private Long articleId;
    @Schema(example = "1")
    private Long userId;
    @Schema(example = "2")
    private Integer likes;
    @Schema(example = "10")
    private Integer views;
    @Schema(example = "5.0")
    private Double rating;

    public Interaction(Long articleId) {
        this.articleId = articleId;
        this.likes = 0;
        this.views = 0;
        this.rating = 0.0;
    }

    public Interaction(Long articleId, Integer likes, Integer views, Double rating) {
        this.articleId = articleId;
        this.likes = likes;
        this.views = views;
        this.rating = rating;
    }
}
