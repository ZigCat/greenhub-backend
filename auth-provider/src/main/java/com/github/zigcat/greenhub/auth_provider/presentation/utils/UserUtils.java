package com.github.zigcat.greenhub.auth_provider.presentation.utils;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
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
}
