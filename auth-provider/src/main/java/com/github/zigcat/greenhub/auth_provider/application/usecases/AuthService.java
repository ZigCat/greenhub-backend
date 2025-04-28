package com.github.zigcat.greenhub.auth_provider.application.usecases;

import com.github.zigcat.greenhub.auth_provider.application.exceptions.BadRequestAppException;
import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.domain.JwtToken;
import com.github.zigcat.greenhub.auth_provider.domain.interfaces.SecurityProvider;
import com.github.zigcat.greenhub.auth_provider.domain.interfaces.UserRepository;
import com.github.zigcat.greenhub.auth_provider.domain.schemas.TokenType;
import com.github.zigcat.greenhub.auth_provider.domain.JwtData;
import com.github.zigcat.greenhub.auth_provider.infrastructure.mappers.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthService {
    private final SecurityProvider securityProvider;
    private final KeyService keyService;
    private final UserRepository adapter;

    public AuthService(SecurityProvider securityProvider, KeyService keyService, UserRepository adapter) {
        this.keyService = keyService;
        this.securityProvider = securityProvider;
        this.adapter = adapter;
    }

    public Mono<AppUser> authorize(String token){
        return keyService.retrieve(securityProvider.getAccessKId(token))
                .flatMap(userKey -> securityProvider.getAccessClaims(
                            token,
                            keyService.decodePublicKey(userKey.getPublicKey())
                        )
                );
    }

    public Mono<AppUser> register(AppUser newUser){
        return adapter.create(UserMapper.toRegisterDTO(newUser))
                .flatMap(user -> keyService.generateKeyPair(user.getEmail())
                        .thenReturn(user));
    }

    public Mono<JwtData> login(ServerHttpRequest request){
        return Mono.fromCallable(() -> request.getHeaders().getFirst("Authorization"))
                .filter(authToken -> authToken != null && authToken.startsWith("Basic"))
                .flatMap(authToken -> adapter.login(authToken.substring(6))
                        .flatMap(this::createTokens));
    }

    public Mono<JwtData> refresh(JwtToken data){
        if(data == null
                || data.getToken() == null
                || data.getTokenType() != TokenType.REFRESH)
            return Mono.error(new BadRequestAppException("Missing or invalid token"));
        String token = data.getToken();
        return securityProvider.getRefreshClaims(token)
                .flatMap(user -> adapter.retrieve(user.getEmail()))
                .flatMap(this::createTokens);
    }

    public Mono<Void> erase(String username){
        return keyService.delete(username);
    }

    private Mono<JwtData> createTokens(AppUser user){
        return keyService.retrieveByUsername(user.getEmail())
                .flatMap(userKey -> Mono.just(new JwtData(
                        securityProvider.generateAccessToken(user, userKey.getId(), keyService.decodePrivateKey(userKey.getPrivateKey())),
                        securityProvider.generateRefreshToken(user),
                        user
                )));
    }
}
