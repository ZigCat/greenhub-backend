package com.github.zigcat.greenhub.user_provider.utils;

import com.github.zigcat.greenhub.user_provider.dto.UserDTO;
import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import org.springframework.stereotype.Component;

public class UserUtils {
    public static AppUser toUser(UserDTO userDTO){
        return new AppUser(
                userDTO.getFname(),
                userDTO.getLname(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getRole()
        );
    }
}
