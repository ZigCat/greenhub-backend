package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.ArticleService;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public/article")
public class ArticlePublicController {
    private final ArticleService service;

    public ArticlePublicController(ArticleService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DTO.ArticleGetDTO>> getById(
            ServerHttpRequest request,
            @PathVariable("id") Long id
    ){
        return service.listById(request, id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<DTO.ArticleGetDTO> getAll(
            ServerHttpRequest request,
            @RequestParam(required = false) Long creator){
        return service.list(request, "GRANTED", creator);
    }
}
