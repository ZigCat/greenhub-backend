package com.github.zigcat.greenhub.auth_provider.infrastructure.mappers;

import com.github.zigcat.greenhub.auth_provider.domain.UserKey;
import com.github.zigcat.greenhub.auth_provider.infrastructure.models.UserKeyModel;

public class KeyMapper {
    public static UserKey toEntity(UserKeyModel model){
        return new UserKey(
                model.getId(),
                model.getUsername(),
                model.getPublicKey(),
                model.getPrivateKey()
        );
    }

    public static UserKeyModel toModel(UserKey entity){
        return new UserKeyModel(
                entity.getId(),
                entity.getUsername(),
                entity.getPublicKey(),
                entity.getPrivateKey()
        );
    }
}
