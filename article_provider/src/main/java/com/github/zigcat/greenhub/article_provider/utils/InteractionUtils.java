package com.github.zigcat.greenhub.article_provider.utils;

import com.github.zigcat.greenhub.article_provider.domain.Interaction;
import com.github.zigcat.greenhub.article_provider.infrastructure.projections.InteractionProjection;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.InteractionModel;

public class InteractionUtils {
    public static InteractionProjection toProjection(InteractionModel model){
        InteractionProjection projection = new InteractionProjection();
        projection.calculateScore(model.getLike(), model.getViews(), model.getRating());
        projection.setId(model.getId());
        projection.setUserId(model.getUserId());
        projection.setArticleId(model.getArticleId());
        return projection;
    }

    public static Interaction toEntity(InteractionModel model){
        return new Interaction(
                model.getArticleId(),
                model.getUserId(),
                model.getLike() ? 1 : 0,
                model.getViews(),
                model.getRating() != null ? model.getRating().doubleValue() : 0.0
        );
    }

    public static Interaction toEntity(
            Long articleId,
            Integer like,
            Integer view,
            Integer rating
    ){
        return new Interaction(
                articleId,
                like,
                view,
                rating != null ? rating.doubleValue() : 0.0
        );
    }
}
