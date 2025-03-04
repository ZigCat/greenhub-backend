package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.InteractionService;
import com.github.zigcat.greenhub.article_provider.domain.Interaction;
import com.github.zigcat.greenhub.article_provider.utils.InteractionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/protected/interact")
public class InteractionController {
    private final InteractionService service;

    public InteractionController(InteractionService service) {
        this.service = service;
    }

    @PostMapping("/{id}")
    public Mono<ResponseEntity<Interaction>> interact(
            @PathVariable("id") Long articleId,
            @RequestParam(required = false) Integer like,
            @RequestParam(required = false) Integer view,
            @RequestParam(required = false) Integer rating,
            ServerHttpRequest request
    ){
        return service.upsert(InteractionUtils.toEntity(articleId, like, view, rating), request)
                .map(ResponseEntity::ok);
    }
}
