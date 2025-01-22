package com.github.zigcat.greenhub.auth_provider.controllers;

import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.auth_provider.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class AuthController {
    private final AuthService service;

    @Autowired
    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<RegisterResponse>> register(@RequestBody RegisterRequest dto){
        return Mono.just(dto)
                .flatMap(data ->
                    service.register(data)
                            .map(response -> new ResponseEntity<>(response, HttpStatus.CREATED))
                );
    }

//    @PostMapping("/login")
//    public Mono<ResponseEntity<>>
}
