package com.github.zigcat.greenhub.api_gateway.config.gateway;

import com.github.zigcat.greenhub.api_gateway.application.filters.GatewayJwtFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfiguration {
    private final GatewayJwtFilter jwtFilter;

    public GatewayRoutesConfiguration(GatewayJwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-provider", r -> r.path("/auth/**")
                        .filters(f -> f.addRequestHeader("X-Request-Source", "Gateway")
                                .stripPrefix(1))
                        .uri("http://localhost:8081"))

                .route("user-provider", r -> r.path("/user/**")
                        .filters(f -> f.filter(jwtFilter.apply(new GatewayJwtFilter.Config()))
                                .addRequestHeader("X-Request-Source", "Gateway")
                                .stripPrefix(1))
                        .uri("http://localhost:8082"))

                .route("platform-protected", r -> r.path("/platform/protected/**")
                        .filters(f -> f.filter(jwtFilter.apply(new GatewayJwtFilter.Config()))
                                .addRequestHeader("X-Request-Source", "Gateway")
                                .stripPrefix(1))
                        .uri("http://localhost:8083"))

                .route("platform-public", r -> r.path("/platform/public/**")
                        .filters(f -> f.addRequestHeader("X-Request-Source", "Gateway")
                                .stripPrefix(1))
                        .uri("http://localhost:8083"))

                .build();
    }
}
