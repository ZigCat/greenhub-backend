package com.github.zigcat.greenhub.user_provider.infrastructure.mappers;

import com.github.zigcat.greenhub.user_provider.domain.Scope;
import com.github.zigcat.greenhub.user_provider.infrastructure.models.ScopeModel;

public class ScopeMapper {
    public static ScopeModel toModel(Scope entity){
        return new ScopeModel(
                entity.getId(),
                entity.getUserId(),
                entity.getScope()
        );
    }

    public static Scope toEntity(ScopeModel model){
        return new Scope(
                model.getId(),
                model.getUserId(),
                model.getScope()
        );
    }
}
