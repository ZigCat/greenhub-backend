package com.github.zigcat.greenhub.api_gateway.config.gateway;

import com.github.zigcat.greenhub.api_gateway.application.filters.GatewayJwtFilter;
import org.apache.http.auth.AUTH;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfiguration {
    private final GatewayJwtFilter jwtFilter;
    @Value("${greenhub.gateway.auth}")
    private String AUTH_URL;
    @Value("${greenhub.gateway.user}")
    private String USER_URL;
    @Value("${greenhub.gateway.main}")
    private String MAIN_URL;
    

    public GatewayRoutesConfiguration(GatewayJwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-provider", r -> r.path("/auth/**")
                        .filters(f -> f.addRequestHeader("X-Request-Source", "Gateway")
                                .stripPrefix(1))
                        .uri(AUTH_URL))
                .route("auth-docs", r -> r.path("/auth/v3/api-docs")
                        .filters(f -> f.stripPrefix(1))
                        .uri(AUTH_URL))

                .route("user-protected", r -> r.path("/user/protected/**")
                        .filters(f -> f.filter(jwtFilter.apply(new GatewayJwtFilter.Config()))
                                .addRequestHeader("X-Request-Source", "Gateway")
                                .stripPrefix(1))
                        .uri(USER_URL))
                .route("user-public", r -> r.path("/user/public/**")
                        .filters(f -> f.addRequestHeader("X-Request-Source", "Gateway")
                                .stripPrefix(1))
                        .uri(USER_URL))
                .route("user-docs", r -> r.path("/user/v3/api-docs")
                        .filters(f -> f.stripPrefix(1))
                        .uri(USER_URL))

                .route("platform-protected", r -> r.path("/platform/protected/**")
                        .filters(f -> f.filter(jwtFilter.apply(new GatewayJwtFilter.Config()))
                                .addRequestHeader("X-Request-Source", "Gateway")
                                .stripPrefix(1))
                        .uri(MAIN_URL))
                .route("platform-public", r -> r.path("/platform/public/**")
                        .filters(f -> f.addRequestHeader("X-Request-Source", "Gateway")
                                .stripPrefix(1))
                        .uri(MAIN_URL))
                .route("platform-docs", r -> r.path("/platform/v3/api-docs")
                        .filters(f -> f.stripPrefix(1))
                        .uri(MAIN_URL))

                .build();
    }
}
