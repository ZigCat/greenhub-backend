package com.github.zigcat.greenhub.article_provider.infrastructure.mappers;

import com.github.zigcat.greenhub.article_provider.domain.AuthorReward;
import com.github.zigcat.greenhub.article_provider.infrastructure.models.AuthorRewardModel;

public class RewardMapper {
    public static AuthorReward toEntity(AuthorRewardModel model){
        return new AuthorReward(
                model.getId(),
                model.getAuthorId(),
                model.getReward(),
                model.getCalculatedAt()
        );
    }

    public static AuthorRewardModel toModel(AuthorReward entity){
        return new AuthorRewardModel(
                entity.getId(),
                entity.getAuthorId(),
                entity.getReward(),
                entity.getCalculatedAt()
        );
    }
}
