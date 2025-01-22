package com.github.zigcat.greenhub.user_provider.utils;

import com.github.zigcat.greenhub.user_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.user_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.user_provider.dto.rest.entities.UserDTO;
import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import com.github.zigcat.greenhub.user_provider.entities.Role;

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

    public static UserDTO toDTO(RegisterRequest user){
        return new UserDTO(
                user.getFname(),
                user.getLname(),
                user.getEmail(),
                user.getPassword(),
                Role.USER.toString()
        );
    }

    public static RegisterResponse toRegResponse(AppUser user){
        return new RegisterResponse(
                user.getId(),
                user.getFname(),
                user.getLname(),
                user.getEmail(),
                user.getRole().toString(),
                user.getRegDate()
        );
    }
}
