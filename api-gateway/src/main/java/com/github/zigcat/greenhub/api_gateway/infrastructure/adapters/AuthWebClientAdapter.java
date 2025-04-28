package com.github.zigcat.greenhub.api_gateway.infrastructure.adapters;

import com.github.zigcat.greenhub.api_gateway.domain.AppUser;
import com.github.zigcat.greenhub.api_gateway.domain.interfaces.AuthRepository;
import com.github.zigcat.greenhub.api_gateway.exceptions.CoreException;
import com.github.zigcat.greenhub.api_gateway.infrastructure.InfrastructureDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthWebClientAdapter implements AuthRepository {
    private final WebClient webClient;

    public AuthWebClientAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://auth-provider").build();
    }

    @Override
    public Mono<AppUser> authorize(InfrastructureDTO.JwtDTO dto) {
        return webClient.post()
                .uri("/webclient/authorize")
                .bodyValue(dto)
                .header("Content-Type", "application/json")
                .header("X-Origin", "api-gateway")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(InfrastructureDTO.ApiError.class)
                                .map(data -> new CoreException(data.message(), data.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(InfrastructureDTO.ApiError.class)
                                .map(data -> new CoreException(data.message(), data.statusCode())))
                .bodyToMono(AppUser.class);
    }
}
