package com.github.zigcat.greenhub.auth_provider.presentation.utils;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.domain.JwtToken;
import com.github.zigcat.greenhub.auth_provider.domain.schemas.TokenType;
import com.github.zigcat.greenhub.auth_provider.presentation.PresentationDTO;

public class UserUtils {
    public static AppUser toEntity(PresentationDTO.UserRegister dto){
        return new AppUser(
                dto.fname(),
                dto.lname(),
                dto.email(),
                dto.password()
        );
    }

    public static JwtToken toEntity(PresentationDTO.JwtToken dto){
        return new JwtToken(
                dto.token(),
                TokenType.valueOf(dto.tokenType())
        );
    }
}
