package com.github.zigcat.greenhub.user_provider.presentation.controllers;

import com.github.zigcat.greenhub.user_provider.application.usecases.ScopeService;
import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.domain.Scope;
import com.github.zigcat.greenhub.user_provider.presentation.PresentationDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/protected")
@Slf4j
@Tag(name = "PROTECTED", description = "User's protected endpoints")
public class ScopeController {
    private final ScopeService service;

    public ScopeController(ScopeService service) {
        this.service = service;
    }

    @Operation(
            summary = "PROMOTE",
            description = "Promote scope to user. Regular users can promote only article.write, other access scopes can be promoted by system or admins.",
            tags = {"Scope"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Scope.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Access Denied",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Access Denied\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":400,\n\"message\":\"Wrong scope param\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal server Error\"}")
                            ))
            },
            parameters = {
                    @Parameter(name = "id", description = "User ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "scope", description = "Scope type", required = true, in = ParameterIn.QUERY)
            },
            security = @SecurityRequirement(name = "Bearer token")
    )
    @PostMapping("/promote/{id}")
    public Mono<ResponseEntity<Scope>> promote(
            @RequestParam String scope,
            @PathVariable("id") Long userId,
            ServerHttpRequest request
    ){
        return service.promote(scope, userId, request)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "DEMOTE",
            description = "Demote scope from user. Only admins can access this endpoint.",
            tags = {"Scope"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Success", content = @Content()),
                    @ApiResponse(responseCode = "403", description = "Access Denied",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Access Denied\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Wrong scope param\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal server Error\"}")
                            ))
            },
            parameters = {
                    @Parameter(name = "id", description = "User ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "scope", description = "Scope type", required = true, in = ParameterIn.QUERY)
            },
            security = @SecurityRequirement(name = "Bearer token")
    )
    @PatchMapping("/demote/{id}")
    public Mono<ResponseEntity<?>> demote(
            @RequestParam String scope,
            @PathVariable("id") Long userId,
            ServerHttpRequest request
    ){
        return service.demote(scope, userId, request)
                .map(res -> ResponseEntity.noContent().build());
    }
}
