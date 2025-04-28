package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webclient")
public class WebClientArticleController {
    private final ArticleService service;

    public WebClientArticleController(ArticleService service) {
        this.service = service;
    }

    @DeleteMapping("/erase/{userId}")
    public Mono<ResponseEntity<?>> erase(@PathVariable("userId") Long userId,
                                         ServerHttpRequest request){
        return service.delete(userId)
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
