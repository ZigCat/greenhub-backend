package com.github.zigcat.greenhub.auth_provider.infrastructure.adapter;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.domain.interfaces.UserRepository;
import com.github.zigcat.greenhub.auth_provider.exceptions.ClientErrorException;
import com.github.zigcat.greenhub.auth_provider.exceptions.ServerErrorException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.BadRequestInfrastructureException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.JwtAuthInfrastructureException;
import com.github.zigcat.greenhub.auth_provider.infrastructure.exceptions.ServerErrorInfrastructureException;
import com.github.zigcat.greenhub.auth_provider.presentation.PresentationDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserWebClientAdapter implements UserRepository {
    private final WebClient webClient;

    public UserWebClientAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://user-provider").build();
    }

    @Override
    public Mono<AppUser> create(InfrastructureDTO.UserRegister dto) {
        return webClient.post()
                .uri("/webclient/create")
                .bodyValue(dto)
                .header("Content-Type", "application/json")
                .header("X-Origin", "auth-provider")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(PresentationDTO.ApiError.class)
                                .map(data -> new ClientErrorException(data.message(), data.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(PresentationDTO.ApiError.class)
                                .map(data -> new ServerErrorException(data.message(), data.statusCode())))
                .bodyToMono(AppUser.class);
    }

    @Override
    public Mono<AppUser> login(String authToken) {
        return webClient.post()
                .uri("/webclient/validate/{authToken}", authToken)
                .header("X-Origin", "auth-provider")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new BadRequestInfrastructureException("Wrong credentials")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new ServerErrorInfrastructureException("Unknown error")))
                .bodyToMono(AppUser.class);
    }

    @Override
    public Mono<AppUser> retrieve(String username){
        return webClient.get()
                .uri("/webclient/retrieve/{username}", username)
                .header("X-Origin", "auth-provider")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new JwtAuthInfrastructureException("Invalid token")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new ServerErrorInfrastructureException("Unknown error")))
                .bodyToMono(AppUser.class);

    }
}
