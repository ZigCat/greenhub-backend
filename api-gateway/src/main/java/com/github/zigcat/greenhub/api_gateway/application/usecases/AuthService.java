package com.github.zigcat.greenhub.api_gateway.application.usecases;

import com.github.zigcat.greenhub.api_gateway.domain.AppUser;
import com.github.zigcat.greenhub.api_gateway.domain.interfaces.AuthRepository;
import com.github.zigcat.greenhub.api_gateway.domain.schemas.TokenType;
import com.github.zigcat.greenhub.api_gateway.infrastructure.InfrastructureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthService {
    private final AuthRepository authorization;

    public AuthService(AuthRepository authorization) {
        this.authorization = authorization;
    }

    public Mono<AppUser> authorizeByToken(String token) {
        log.info("Preparing token {} to share via Kafka", token);
        return authorization.authorize(new InfrastructureDTO.JwtDTO(token, TokenType.ACCESS));
    }
}
