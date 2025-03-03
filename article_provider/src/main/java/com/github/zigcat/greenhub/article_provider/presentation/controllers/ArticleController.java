package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.ArticleService;
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
@RequestMapping("/protected/article")
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

    @GetMapping
    public Flux<DTO.ArticleGetDTO> getAll(
            ServerHttpRequest request,
            @RequestParam(required = false, defaultValue = "GRANTED") String status,
            @RequestParam(required = false) Long creator
            ){
        return service.list(request, status, creator);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<DTO.ArticleGetDTO>> getById(
            ServerHttpRequest request,
            @PathVariable("id") Long id
    ){
        return service.listById(request, id)
                .map(ResponseEntity::ok);
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

    @PatchMapping("/moderate/{id}")
    public Mono<ResponseEntity<Article>> moderate(
            @RequestBody DTO.ArticleModerateDTO dto,
            @PathVariable("id") Long id,
            ServerHttpRequest request
    ){
        return service.moderate(dto, id, request)
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
