package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.CategoryService;
import com.github.zigcat.greenhub.article_provider.domain.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public/category")
public class CategoryPublicController {
    private final CategoryService service;

    public CategoryPublicController(CategoryService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Category>> getById(
            @PathVariable("id") Long id
    ){
        return service.retrieve(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<Category> getAll(){
        return service.list();
    }
}
