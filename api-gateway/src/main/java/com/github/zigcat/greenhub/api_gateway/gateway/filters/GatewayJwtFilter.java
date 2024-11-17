package com.github.zigcat.greenhub.api_gateway.gateway.filters;

import com.github.zigcat.greenhub.api_gateway.exceptions.AuthException;
import com.github.zigcat.greenhub.api_gateway.gateway.dto.UserResponse;
import com.github.zigcat.greenhub.api_gateway.gateway.services.AuthService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayJwtFilter implements GatewayFilterFactory<GatewayJwtFilter.Config> {
    private final AuthService authService;

    @Autowired
    public GatewayJwtFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if(!validateAuthHeader(authHeader)){
                return this.onError(exchange, HttpStatus.UNAUTHORIZED);
            }
            String token = extractToken(authHeader);
            try {
                UserResponse response = authService.authorizeByToken(token);
                exchange.getRequest().mutate().header("X-Username", response.getEmail());
                return chain.filter(exchange);
            } catch (AuthException e){
                return this.onError(exchange, HttpStatus.FORBIDDEN);
            }
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    private boolean validateAuthHeader(String authHeader){
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private String extractToken(String authHeader){
        return authHeader.substring(7);
    }

    @Override
    public Class<Config> getConfigClass() {
        return Config.class;
    }

    @Override
    public Config newConfig() {
        Config c = new Config();
        return c;
    }

    @NoArgsConstructor
    public static class Config {
    }
}
