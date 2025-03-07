package com.github.zigcat.greenhub.user_provider.infrastructure.mappers;

import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.domain.Scope;
import com.github.zigcat.greenhub.user_provider.domain.schemas.Role;
import com.github.zigcat.greenhub.user_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.user_provider.infrastructure.models.UserModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserMapper {
    public static Mono<InfrastructureDTO.UserAuth> mapAuthRows(Flux<InfrastructureDTO.UserAuth> rowsFlux){
        return rowsFlux.collectList().map(rows -> {
            if (rows.isEmpty()) return null;
            Long id = rows.get(0).id();
            String email = rows.get(0).email();
            String role = rows.get(0).role();
            List<String> scopes = rows.stream()
                    .map(InfrastructureDTO.UserAuth::scopes)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            String scope = String.join(" ", scopes);
            return new InfrastructureDTO.UserAuth(id, email, role, scope);
        });
    }

    public static AppUser toEntity(UserModel model, List<Scope> scopes){
        return new AppUser(
                model.getId(),
                model.getFname(),
                model.getLname(),
                model.getEmail(),
                model.getPassword(),
                model.getRole(),
                model.getRegDate(),
                scopes == null ? null : scopes.stream()
                        .map(Scope::getScope)
                        .collect(Collectors.joining(" "))
        );
    }

    public static AppUser toEntity(InfrastructureDTO.UserAuth dto){
        return new AppUser(
                dto.id(),
                dto.email(),
                Role.valueOf(dto.role()),
                dto.scopes()
        );
    }

    public static AppUser toEntity(InfrastructureDTO.UserRegister dto){
        return new AppUser(
                dto.fname(),
                dto.lname(),
                dto.email(),
                dto.password()
        );
    }

    public static AppUser toEntity(InfrastructureDTO.UserLogin dto){
        return new AppUser(
                dto.username(),
                dto.password()
        );
    }

    public static UserModel toModel(AppUser entity){
        return new UserModel(
                entity.getId(),
                entity.getFname(),
                entity.getLname(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.getRegDate()
        );
    }
}
