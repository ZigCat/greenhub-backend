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

@RestController
@Slf4j
public class AuthController {
    private final AuthService service;

    @Autowired
    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<UserRegisterResponse>> register(@RequestBody UserRegisterRequest dto){
        System.out.println("request accepted");
        UserRegisterResponse response = service.processRegistration(dto);
        return Mono.just(ResponseEntity.ok(response));
    }
}
