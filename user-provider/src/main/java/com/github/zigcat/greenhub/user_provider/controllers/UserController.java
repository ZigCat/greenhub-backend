package com.github.zigcat.greenhub.user_provider.controllers;

import com.github.zigcat.greenhub.user_provider.dto.rest.entities.UserDTO;
import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import com.github.zigcat.greenhub.user_provider.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<?>> create(@RequestBody Mono<UserDTO> dto){
        return Mono.just(new ResponseEntity<>(service.create(dto), HttpStatus.CREATED));
    }

    @GetMapping("/id")
    public Mono<ResponseEntity<AppUser>> getById(@RequestParam Long id){
        return service.retrieve(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<AppUser> getAll(){
        return service.list();
    }
}
