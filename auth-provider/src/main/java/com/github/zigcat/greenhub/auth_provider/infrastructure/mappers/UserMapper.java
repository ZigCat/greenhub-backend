package com.github.zigcat.greenhub.auth_provider.infrastructure.mappers;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.infrastructure.InfrastructureDTO;

public class UserMapper {
    public static InfrastructureDTO.UserRegister toRegisterDTO(AppUser user){
        return new InfrastructureDTO.UserRegister(
                user.getFname(),
                user.getLname(),
                user.getEmail(),
                user.getPassword()
        );
    }

    public static InfrastructureDTO.UserLogin toLoginDTO(AppUser user){
        return new InfrastructureDTO.UserLogin(
                user.getEmail(),
                user.getPassword()
        );
    }

    public static InfrastructureDTO.UserAuth toAuthDTO(AppUser user){
        return new InfrastructureDTO.UserAuth(
                user.getId(),
                user.getEmail(),
                user.getRole().toString(),
                user.getScopes()
        );
    }
}
