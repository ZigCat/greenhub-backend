package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.ArticleService;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.utils.ArticleUtils;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/protected/article")
@Tag(name = "PROTECTED", description = "Article's protected endpoints")
public class ArticleController {
    private final ArticleService service;

    public ArticleController(
            ArticleService service
    ) {
        this.service = service;
    }

    @Operation(
            summary = "GET ALL (protected)",
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
                    @Parameter(name = "status", description = "Article status", in = ParameterIn.QUERY),
                    @Parameter(name = "creator", description = "Article's author", in = ParameterIn.QUERY),
            },
            security = @SecurityRequirement(name = "Bearer token")
    )
    @GetMapping
    public Flux<Article> getAll(
            ServerHttpRequest request,
            @RequestParam(required = false, defaultValue = "GRANTED") String status,
            @RequestParam(required = false) Long creator
            ){
        return service.list(request, status, creator);
    }

    @Operation(
            summary = "GET BY ID (protected)",
            description = "Retrieving article by ID",
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
                    @Parameter(name = "id", description = "Article's ID", required = true, in = ParameterIn.PATH)
            },
            security = @SecurityRequirement(name = "Bearer token")
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
            summary = "GET RECOMMENDATIONS",
            description = "Listing recommended articleS",
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
            security = @SecurityRequirement(name = "Bearer token")
    )
    @GetMapping("/recommend")
    public Flux<Article> getRecommendations(ServerHttpRequest request){
        return service.listRecommended(request);
    }

    @Operation(
            summary = "CREATE",
            description = "Creating article",
            tags = {"Article"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
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
                    @ApiResponse(responseCode = "403", description = "Forbidden request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Access denied\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":404,\n\"message\":\"Articles not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "409", description = "Conflict",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":409,\n\"message\":\"Data conflict occurred while trying to transact\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":503,\n\"message\":\"Article service unavailable\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal server error\"}")
                            ))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Article DTO",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DTO.ArticleDTO.class)
                    )
            ),
            security = @SecurityRequirement(name = "Bearer token")
    )
    @PostMapping
    public Mono<ResponseEntity<Article>> create(
            @RequestBody DTO.ArticleDTO dto,
            ServerHttpRequest request
    ){
        return service.create(ArticleUtils.toEntity(dto), request)
                .map(entity -> new ResponseEntity<>(entity, HttpStatus.CREATED));
    }

    @Operation(
            summary = "UPDATE",
            description = "Updating article by ID",
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
                    @ApiResponse(responseCode = "403", description = "Forbidden request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Access denied\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":404,\n\"message\":\"Articles not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "409", description = "Conflict",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":409,\n\"message\":\"Data conflict occurred while trying to transact\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":503,\n\"message\":\"Article service unavailable\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal server error\"}")
                            ))
            },
            parameters = {
                    @Parameter(name = "id", description = "Article's ID", required = true, in = ParameterIn.PATH)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Article DTO",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DTO.ArticleDTO.class)
                    )
            ),
            security = @SecurityRequirement(name = "Bearer token")
    )
    @PatchMapping("/{id}")
    public Mono<ResponseEntity<Article>> update(
            @RequestBody DTO.ArticleDTO dto,
            @PathVariable("id") Long id,
            ServerHttpRequest request
    ){
        return service.update(ArticleUtils.toEntity(dto), id, request)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "MODERATE",
            description = "Moderate article's visibility",
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
                    @ApiResponse(responseCode = "403", description = "Forbidden request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Access denied\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":404,\n\"message\":\"Articles not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":503,\n\"message\":\"Article service unavailable\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal server error\"}")
                            ))
            },
            parameters = {
                    @Parameter(name = "id", description = "Article's ID", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "status", description = "Article Status", required = true, in = ParameterIn.QUERY)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Article DTO",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DTO.ArticleDTO.class)
                    )
            ),
            security = @SecurityRequirement(name = "Bearer token")
    )
    @PatchMapping("/moderate/{id}")
    public Mono<ResponseEntity<Article>> moderate(
            @RequestParam String status,
            @PathVariable("id") Long id,
            ServerHttpRequest request
    ){
        return service.moderate(status, id, request)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "DELETE",
            description = "Delete article by ID",
            tags = {"Article"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content",
                            content = @Content()
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":400,\n\"message\":\"Wrong param\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Access denied\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":404,\n\"message\":\"Articles not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "503", description = "Service unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":503,\n\"message\":\"Article service unavailable\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal server error\"}")
                            ))
            },
            parameters = {
                    @Parameter(name = "id", description = "Article's ID", required = true, in = ParameterIn.PATH)
            },
            security = @SecurityRequirement(name = "Bearer token")
    )
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<?>> delete(
            @PathVariable("id") Long id,
            ServerHttpRequest request
    ){
        return service.delete(id, request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
