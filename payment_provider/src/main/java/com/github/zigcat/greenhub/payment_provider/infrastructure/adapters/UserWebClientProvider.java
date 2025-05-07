package com.github.zigcat.greenhub.payment_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.payment_provider.domain.interfaces.UserProvider;
import com.github.zigcat.greenhub.payment_provider.domain.schemas.ScopeType;
import com.github.zigcat.greenhub.payment_provider.exceptions.ClientErrorException;
import com.github.zigcat.greenhub.payment_provider.exceptions.ServerErrorException;
import com.github.zigcat.greenhub.payment_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.payment_provider.presentation.PresentationDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserWebClientProvider implements UserProvider {
    private final WebClient webClient;

    public UserWebClientProvider(WebClient.Builder loadBalancedBuilder) {
        this.webClient = loadBalancedBuilder.baseUrl("lb://user-provider").build();
    }

    @Override
    public Mono<InfrastructureDTO.ScopeDTO> promote(Long userId) {
        return webClient.post()
                .uri("/protected/promote/"+userId+"?scope="+ ScopeType.PAYMENT_VIEW.getScope())
                .header("X-Origin", "payment-provider")
                .header("X-User-Role", "ADMIN")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(PresentationDTO.ApiError.class)
                                .map(data -> new ClientErrorException(data.message(), data.status())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(PresentationDTO.ApiError.class)
                                .map(data -> new ServerErrorException(data.message(), data.status())))
                .bodyToMono(InfrastructureDTO.ScopeDTO.class);
    }
}
