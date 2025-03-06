package com.github.zigcat.greenhub.user_provider.presentation.utils;

import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.presentation.PresentationDTO;

public class UserUtils {
    public static AppUser toEntity(PresentationDTO.UserDTO dto){
        return new AppUser(
                dto.fname(),
                dto.lname(),
                dto.email(),
                dto.password()
        );
    }
}
