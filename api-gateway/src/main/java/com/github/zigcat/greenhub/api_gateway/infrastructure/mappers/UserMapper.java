package com.github.zigcat.greenhub.api_gateway.infrastructure.mappers;

import com.github.zigcat.greenhub.api_gateway.domain.AppUser;
import com.github.zigcat.greenhub.api_gateway.domain.schemas.Role;
import com.github.zigcat.greenhub.api_gateway.infrastructure.InfrastructureDTO;

public class UserMapper {
    public static AppUser toEntity(InfrastructureDTO.UserAuth dto){
        return new AppUser(
                dto.id(),
                dto.email(),
                Role.valueOf(dto.role()),
                dto.scopes()
        );
    }
}
