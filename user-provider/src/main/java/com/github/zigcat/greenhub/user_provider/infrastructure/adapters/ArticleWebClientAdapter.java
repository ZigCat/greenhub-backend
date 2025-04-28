package com.github.zigcat.greenhub.user_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.user_provider.domain.interfaces.ArticleRepository;
import com.github.zigcat.greenhub.user_provider.exceptions.ClientErrorException;
import com.github.zigcat.greenhub.user_provider.exceptions.ServerErrorException;
import com.github.zigcat.greenhub.user_provider.presentation.PresentationDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ArticleWebClientAdapter implements ArticleRepository {
    private final WebClient webClient;

    public ArticleWebClientAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("lb://article-provider").build();
    }

    @Override
    public Mono<Void> erase(Long userId) {
        return webClient.delete()
                .uri("/webclient/erase/{userId}", userId)
                .header("X-Origin", "auth-provider")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(PresentationDTO.ApiError.class)
                                .map(data -> new ClientErrorException(data.message(), data.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(PresentationDTO.ApiError.class)
                                .map(data -> new ServerErrorException(data.message(), data.statusCode())))
                .bodyToMono(Void.class);
    }
}
