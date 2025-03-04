package com.github.zigcat.greenhub.article_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.article_provider.domain.AppUser;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.UserRepository;
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
    public Mono<AppUser> retrieve(Long id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(AppUser.class);
    }
}
