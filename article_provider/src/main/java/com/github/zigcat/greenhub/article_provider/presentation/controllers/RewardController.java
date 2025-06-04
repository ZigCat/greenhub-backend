package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.RewardService;
import com.github.zigcat.greenhub.article_provider.domain.AuthorReward;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/protected/reward")
public class RewardController {
    private final RewardService service;

    public RewardController(RewardService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Flux<AuthorReward> getByAuthorId(ServerHttpRequest request, @PathVariable("id") Long id){
        return service.retrieveByAuthorId(request, id);
    }

    @GetMapping
    public Flux<AuthorReward> calculate(ServerHttpRequest request){
        return service.calculateImmediately(request);
    }
}
