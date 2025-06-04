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
@RequestMapping("/public/reward")
public class RewardControllerPublic {
    private final RewardService service;

    public RewardControllerPublic(RewardService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<AuthorReward> calculate(){
        return service.calculateMonthlyReward();
    }
}
