package com.github.zigcat.greenhub.article_provider.infrastructure.adapters;

import com.github.zigcat.greenhub.article_provider.domain.AppUser;
import com.github.zigcat.greenhub.article_provider.domain.interfaces.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UserWebClientAdapter implements UserRepository {
    private final WebClient webClient;

    public UserWebClientAdapter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://user-provider").build();
    }

    @Override
    public Mono<AppUser> retrieve(Long id) {
        return webClient.get()
                .uri("public/{id}", id)
                .retrieve()
                .bodyToMono(AppUser.class);
    }

    @Override
    public Mono<AppUser> promote(Long id) {
        return webClient.post()
                .uri("protected/promote")
                .header("X-User-Role", "ADMIN")
                .header("X-User-Id", id.toString())
                .retrieve()
                .bodyToMono(AppUser.class);
    }

    @Override
    public Flux<AppUser> listByIds(List<Long> ids) {
        return webClient.post()
                .uri("/public/specified")
                .bodyValue(ids)
                .retrieve()
                .bodyToFlux(AppUser.class);
    }
}
