package com.github.zigcat.greenhub.auth_provider.domain.interfaces;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.infrastructure.InfrastructureDTO;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<AppUser> create(InfrastructureDTO.UserRegister dto);
    Mono<AppUser> login(String authToken);
    Mono<AppUser> retrieve(String username);
}
