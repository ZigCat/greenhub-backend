package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.InteractionService;
import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.domain.Interaction;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import com.github.zigcat.greenhub.article_provider.utils.InteractionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
@RequestMapping("/protected/interact")
@Tag(name = "PROTECTED", description = "Article's protected endpoints")
public class InteractionController {
    private final InteractionService service;

    public InteractionController(InteractionService service) {
        this.service = service;
    }

    @Operation(
            summary = "INTERACT",
            description = "Interacting with article",
            tags = {"Article"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Interaction.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":400,\n\"message\":\"Wrong param\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Not Found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":404,\n\"message\":\"Articles not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "503", description = "Service Unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":503,\n\"message\":\"Article service unavailable\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal server Error\"}")
                            ))
            },
            parameters = {
                    @Parameter(name = "id", description = "Article ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "like", description = "Like", in = ParameterIn.QUERY),
                    @Parameter(name = "view", description = "View", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "rating", description = "Rating", in = ParameterIn.QUERY)
            },
            security = @SecurityRequirement(name = "Bearer token")
    )
    @PostMapping("/{id}")
    public Mono<ResponseEntity<Interaction>> interact(
            @PathVariable("id") Long articleId,
            @RequestParam(required = false) Integer like,
            @RequestParam Integer view,
            @RequestParam(required = false) Integer rating,
            ServerHttpRequest request
    ){
        return service.upsert(InteractionUtils.toEntity(articleId, like, view, rating), request)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "GET BY IDs",
            description = "Retrieving interaction by articleId and userId",
            tags = {"Article"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Interaction.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":400,\n\"message\":\"Wrong param\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Access denied\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Not Found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":404,\n\"message\":\"Interaction not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "503", description = "Service Unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":503,\n\"message\":\"Article service unavailable\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal server Error\"}")
                            ))
            },
            parameters = {
                    @Parameter(name = "userId", description = "User ID", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "userId", description = "User ID", required = true, in = ParameterIn.QUERY)
            },
            security = @SecurityRequirement(name = "Bearer token")
    )
    @GetMapping
    public Mono<ResponseEntity<Interaction>> getByUserIdAndArticleId(
            @RequestParam Long user,
            @RequestParam Long article,
            ServerHttpRequest request
    ){
        return service.retrieveByUserAndArticle(user, article, request)
                .map(ResponseEntity::ok);
    }
}
