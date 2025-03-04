package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.CategoryService;
import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.utils.CategoryUtils;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/protected/category")
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<Category>> create(
            @RequestBody DTO.CategoryDTO dto,
            ServerHttpRequest request
    ){
        return service.create(CategoryUtils.toEntity(dto), request)
                .map(entity -> new ResponseEntity<>(entity, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Category>> update(
            @PathVariable("id") Long id,
            @RequestBody DTO.CategoryDTO dto,
            ServerHttpRequest request
            ){
        return service.update(id, CategoryUtils.toEntity(dto), request)
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
