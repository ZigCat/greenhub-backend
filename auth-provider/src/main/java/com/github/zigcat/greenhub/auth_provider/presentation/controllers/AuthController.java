package com.github.zigcat.greenhub.auth_provider.presentation.controllers;

import com.github.zigcat.greenhub.auth_provider.domain.AppUser;
import com.github.zigcat.greenhub.auth_provider.domain.JwtData;
import com.github.zigcat.greenhub.auth_provider.application.usecases.AuthService;
import com.github.zigcat.greenhub.auth_provider.presentation.PresentationDTO;
import com.github.zigcat.greenhub.auth_provider.presentation.utils.UserUtils;
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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
            summary = "REGISTRATION",
            description = "Creates new user based on data that provided in request body",
            tags = {"Auth"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppUser.class)
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class)
                            )),
                    @ApiResponse(responseCode = "503", description = "User service/source unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class)
                            ))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New user's data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PresentationDTO.UserRegister.class)
                    )
            )
    )
    @PostMapping("/register")
    public Mono<ResponseEntity<AppUser>> register(
            @RequestBody PresentationDTO.UserRegister dto
    ){
        return service.register(UserUtils.toEntity(dto))
                .map(entity -> new ResponseEntity<>(entity, HttpStatus.CREATED));
    }

    @Operation(
            summary = "LOGIN",
            description = "Getting access and refresh tokens by providing user credentials",
            tags = {"Auth"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtData.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Missing required data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class)
                            )),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class)
                            )),
                    @ApiResponse(responseCode = "503", description = "User service/source unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class)
                            ))
            },
            security = @SecurityRequirement(name = "basicAuth")
    )
    @PostMapping("/login")
    public Mono<ResponseEntity<JwtData>> login(
            ServerHttpRequest request
    ){
        return service.login(request)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "REFRESH",
            description = "Updating tokens by providing unexpired refresh token",
            tags = {"Auth"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtData.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Missing required data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class)
                            )),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class)
                            ))
            }
    )
    @PostMapping("/refresh")
    public Mono<ResponseEntity<JwtData>> refresh(
            @RequestBody PresentationDTO.JwtToken dto
            ){
        return service.refresh(UserUtils.toEntity(dto))
                .map(ResponseEntity::ok);
    }
}
