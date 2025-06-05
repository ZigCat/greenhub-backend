package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.ArticleService;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public/article")
@Tag(name = "PUBLIC", description = "Article's public endpoints")
public class ArticlePublicController {
    private final ArticleService service;

    public ArticlePublicController(ArticleService service) {
        this.service = service;
    }

    @GetMapping("/search")
    public Flux<Article> search(@RequestParam String query){
        return service.search(query, false);
    }

    @Operation(
            summary = "GET BY ID",
            description = "Retrieving article by ID, optionally by criteria",
            tags = {"Article"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Article.class)
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
                    @Parameter(name = "id", description = "Article ID", required = true, in = ParameterIn.PATH)
            }
    )
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Article>> getById(
            ServerHttpRequest request,
            @PathVariable("id") Long id
    ){
        return service.retrieve(request, id)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "GET ALL",
            description = "Listing all articles, optionally by criteria",
            tags = {"Article"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Article.class)
                                    )
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
                    @Parameter(name = "creator", description = "Article's author", in = ParameterIn.QUERY)
            }
    )
    @GetMapping
    public Flux<Article> getAll(
            ServerHttpRequest request,
            @RequestParam(required = false) Long creator,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Boolean sort,
            @RequestParam(required = false) Long category){
        return service.list(request, "GRANTED", "FREE", creator, category, page, size, sort);
    }
}
