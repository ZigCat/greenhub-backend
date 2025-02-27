package com.github.zigcat.greenhub.article_provider.infrastructure.utils;

import com.github.zigcat.greenhub.article_provider.domain.Interaction;
import com.github.zigcat.greenhub.article_provider.domain.InteractionProjection;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.InteractionModel;

public class InteractionUtils {
    public static Interaction toEntity(InteractionModel model){
        return new Interaction(
                model.getId(),
                model.getUserId(),
                model.getArticleId(),
                model.isLike(),
                model.isStar(),
                model.getViews(),
                model.getRating()
        );
    }

    public static InteractionProjection toProjection(Interaction interaction){
        float score = interaction.calculateScore();
        return new InteractionProjection(
                interaction.getId(),
                interaction.getUserId(),
                interaction.getArticleId(),
                score
        );
    }
}
