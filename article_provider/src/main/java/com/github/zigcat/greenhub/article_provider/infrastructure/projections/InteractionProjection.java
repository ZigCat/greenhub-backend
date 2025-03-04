package com.github.zigcat.greenhub.article_provider.infrastructure.projections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InteractionProjection {
    private String id;
    private Long userId;
    private Long articleId;
    private Float score;

    public void calculateScore(boolean like, Integer views, Integer rating){
        float score = 0;
        if (like) score += 3.0F;
        if (views != null) score += (float) (views * 0.01);
        if (rating != null) score += rating;
        this.score = score;
    }
}
