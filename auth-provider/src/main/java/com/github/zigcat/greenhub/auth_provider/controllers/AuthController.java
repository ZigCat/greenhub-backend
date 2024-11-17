package com.github.zigcat.greenhub.auth_provider.controllers;

import com.github.zigcat.greenhub.auth_provider.dto.requests.UserRegisterRequest;
import com.github.zigcat.greenhub.auth_provider.dto.responses.UserRegisterResponse;
import com.github.zigcat.greenhub.auth_provider.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@Slf4j
public class AuthController {
    private final AuthService service;

    @Autowired
    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<UserRegisterResponse>> register(){
        return Mono.just(ResponseEntity.ok(new UserRegisterResponse(1L, "John", "Doe", "jdoe@example.com", "ADMIN", LocalDateTime.now())));
    }
}
