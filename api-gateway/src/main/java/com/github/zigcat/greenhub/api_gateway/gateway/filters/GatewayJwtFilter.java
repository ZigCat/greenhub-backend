package com.github.zigcat.greenhub.api_gateway.gateway.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zigcat.greenhub.api_gateway.dto.message.ApiError;
import com.github.zigcat.greenhub.api_gateway.exceptions.AuthException;
import com.github.zigcat.greenhub.api_gateway.exceptions.ServerException;
import com.github.zigcat.greenhub.api_gateway.gateway.services.AuthService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GatewayJwtFilter implements GatewayFilterFactory<GatewayJwtFilter.Config> {
    private final AuthService authService;
    private final PathMatcher pathMatcher;
    private final ObjectMapper objectMapper;
    @Autowired
    public GatewayJwtFilter(AuthService authService) {
        this.authService = authService;
        this.pathMatcher = new AntPathMatcher();
        this.objectMapper = new ObjectMapper();
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if(skipPath(exchange)){
                return chain.filter(exchange);
            }
            if (!validateAuthHeader(authHeader)) {
                return this.onError(exchange, HttpStatus.UNAUTHORIZED, "JWT token is wrong or missing");
            }
            String token = extractToken(authHeader);
            log.info("Token is valid "+token);
            return authService.authorizeByToken(token)
                    .flatMap(response -> {
                        exchange.getRequest().mutate().header("X-Username", response.getEmail());
                        exchange.getRequest().mutate().header("X-Scopes", response.getScopes());
                        exchange.getRequest().mutate().header("X-Role", response.getRole());
                        return chain.filter(exchange);
                    })
                    .onErrorResume(AuthException.class, e -> this.onError(exchange, HttpStatus.UNAUTHORIZED, e.getMessage()))
                    .onErrorResume(ServerException.class, e -> this.onError(exchange, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        };
    }

    private boolean skipPath(ServerWebExchange exchange){
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();
        return (pathMatcher.match("/user/v3/api-docs", path)
                || (pathMatcher.match("/user**", path) && method.matches("GET"))
                || (pathMatcher.match("/user/**", path) && method.matches("GET")));
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ApiError apiError = new ApiError(status.value(), message);
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(apiError);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.setComplete();
        }
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
