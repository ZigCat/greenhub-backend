package com.github.zigcat.greenhub.user_provider.utils;

import com.github.zigcat.greenhub.user_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.user_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.user_provider.dto.mq.responses.UserAuthResponse;
import com.github.zigcat.greenhub.user_provider.dto.rest.entities.UserDTO;
import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import com.github.zigcat.greenhub.user_provider.entities.Role;
import com.github.zigcat.greenhub.user_provider.entities.Scope;
import io.r2dbc.spi.Row;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static Mono<UserAuthResponse> mapAuthRows(Flux<UserAuthResponse> rowsFlux){
        return rowsFlux.collectList().map(rows -> {
            if (rows.isEmpty()) return null;
            UserAuthResponse user = rows.get(0);
            List<String> scopes = rows.stream()
                    .map(UserAuthResponse::getScopes)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            user.setScopes(String.join(" ", scopes));
            return user;
        });
    }

    public static List<Scope> defaultUserScopes(Long userId){
        return List.of(
                new Scope(userId, "articles.read"),
                new Scope(userId, "users.read")
        );
    }
}
