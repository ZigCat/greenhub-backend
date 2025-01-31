package com.github.zigcat.greenhub.auth_provider.controllers;

import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.JwtRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.auth_provider.dto.mq.responses.RegisterResponse;
import com.github.zigcat.greenhub.auth_provider.dto.rest.JwtResponse;
import com.github.zigcat.greenhub.auth_provider.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Base64;

@RestController
@Slf4j
@Tag(name = "Auth", description = "Authorization processes related controller")
public class AuthController {
    private final AuthService service;

    @Autowired
    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(
            summary = "New user registering",
            description = "Creates new user based on data that provided in request body",
            tags = {"Auth"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RegisterResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New user's data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterRequest.class)
                    )
            )
    )
    @PostMapping("/register")
    public Mono<ResponseEntity<RegisterResponse>> register(@RequestBody RegisterRequest dto){
        return Mono.just(dto)
                .flatMap(data ->
                    service.register(data)
                            .map(response -> new ResponseEntity<>(response, HttpStatus.CREATED))
                );
    }

    @Operation(
            summary = "Login into service",
            description = "Getting access and refresh tokens by providing user credentials",
            tags = {"Auth"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Missing required data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = @SecurityRequirement(name = "basicAuth")
    )
    @PostMapping("/login")
    public Mono<ResponseEntity<JwtResponse>> login(@RequestHeader("Authorization") String authHeader){
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            log.info("LOGIN PROCESS");
            String base64Credentials = authHeader.substring(6);
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            String[] parts = credentials.split(":", 2);
            String username = parts[0];
            String password = parts[1];
            return service.login(username, password)
                    .map(ResponseEntity::ok);
        }
        return Mono.just(ResponseEntity.badRequest().build());
    }
}
