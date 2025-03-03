package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.ArticleService;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public")
public class ArticlePublicController {
    private final ArticleService service;

    public ArticlePublicController(ArticleService service) {
        this.service = service;
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
}
