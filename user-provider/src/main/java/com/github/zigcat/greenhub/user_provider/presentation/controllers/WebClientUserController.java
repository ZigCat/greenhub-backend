package com.github.zigcat.greenhub.user_provider.presentation.controllers;

import com.github.zigcat.greenhub.user_provider.application.usecases.UserService;
import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.presentation.PresentationDTO;
import com.github.zigcat.greenhub.user_provider.presentation.utils.UserUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webclient")
public class WebClientUserController {
    private final UserService service;

    public WebClientUserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<AppUser>> create(@RequestBody PresentationDTO.UserDTO dto,
                                                ServerHttpRequest request){
        return service.register(UserUtils.toEntity(dto))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/validate/{authToken}")
    public Mono<ResponseEntity<AppUser>> validate(@PathVariable("authToken") String authToken,
                                                  ServerHttpRequest request){
        return service.login(authToken)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/retrieve/{username}")
    public Mono<ResponseEntity<AppUser>> retrieve(@PathVariable("username") String username,
                                                  ServerHttpRequest request){
        return service.retrieveByEmailWithScopes(username)
                .map(ResponseEntity::ok);
    }
}
