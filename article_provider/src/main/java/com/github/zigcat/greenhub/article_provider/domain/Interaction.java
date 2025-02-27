package com.github.zigcat.greenhub.article_provider.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Interaction {
    private String id;
    private Long userId;
    private Long articleId;
    private boolean like;
    private boolean star;
    private Integer views;
    private Integer rating;

    public float calculateScore(){
        float score = 0;
        if (like) score += 2.0;
        if (star) score += 3.0;
        if (views != null) score += (float) (views * 0.01);
        if (rating != null) score += rating;
        return score;
    }
}
