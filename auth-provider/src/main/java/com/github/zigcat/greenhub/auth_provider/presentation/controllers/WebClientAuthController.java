package com.github.zigcat.greenhub.auth_provider.presentation.controllers;

import com.github.zigcat.greenhub.auth_provider.application.usecases.AuthService;
import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.infrastructure.InfrastructureDTO;
import com.github.zigcat.greenhub.auth_provider.infrastructure.mappers.UserMapper;
import com.github.zigcat.greenhub.auth_provider.presentation.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/webclient")
public class WebClientAuthController {
    private final AuthService service;

    public WebClientAuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/authorize")
    public Mono<ResponseEntity<AppUser>> authorize(@RequestBody InfrastructureDTO.JwtDTO dto,
                                                   ServerHttpRequest request){
        return service.authorize(dto.token())
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/erase/{username}")
    public Mono<ResponseEntity<?>> deleteKey(@PathVariable("username") String username,
                                             ServerHttpRequest request){
        return service.erase(username)
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
