package com.github.zigcat.greenhub.article_provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Interaction {
    private Long articleId;
    private Long userId;
    private Integer likes;
    private Integer views;
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
