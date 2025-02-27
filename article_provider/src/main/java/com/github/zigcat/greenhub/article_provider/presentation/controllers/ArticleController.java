package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.ArticleService;
import com.github.zigcat.greenhub.article_provider.application.usecases.PermissionService;
import com.github.zigcat.greenhub.article_provider.application.usecases.RecommendationService;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/article")
public class ArticleController {
    private final ArticleService service;
    private final RecommendationService recommendationService;

    public ArticleController(
            ArticleService service,
            RecommendationService recommendationService
    ) {
        this.service = service;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Article>> getById(@PathVariable("id") Long id){
        return service.retrieve(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Article> getAll(){
        return service.list();
    }

    @GetMapping("/recommend")
    public Flux<Article> getRecommendations(ServerHttpRequest request){
        return recommendationService.getRecommendations(request);
    }

    @PostMapping
    public Mono<ResponseEntity<Article>> create(
            @RequestBody DTO.ArticleCreateDTO dto,
            ServerHttpRequest request
    ){
        return service.create(dto, request)
                .map(entity -> new ResponseEntity<>(entity, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Article>> update(
            @RequestBody DTO.ArticleCreateDTO dto,
            @PathVariable("id") Long id,
            ServerHttpRequest request
    ){
        return service.update(dto, id, request)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<?>> delete(
            @PathVariable("id") Long id,
            ServerHttpRequest request
    ){
        return service.delete(id, request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
