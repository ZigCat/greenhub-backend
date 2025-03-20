package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.CategoryService;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.utils.CategoryUtils;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/protected/category")
@Tag(name = "PROTECTED", description = "Category's protected endpoints")
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @Operation(
            summary = "CREATE",
            description = "Create category (only admin can access)",
            tags = {"Category"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Category.class)
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
                                    examples = @ExampleObject(value = "{\"code\":400,\n\"message\":\"Access denied\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "409", description = "Conflict",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":409,\n\"message\":\"Data conflict occurred while trying to transact\"}")
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
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category DTO",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DTO.CategoryDTO.class)
                    )
            ),
            security = @SecurityRequirement(name = "Bearer token")
    )
    @PostMapping
    public Mono<ResponseEntity<Category>> create(
            @RequestBody DTO.CategoryDTO dto,
            ServerHttpRequest request
    ){
        return service.create(CategoryUtils.toEntity(dto), request)
                .map(entity -> new ResponseEntity<>(entity, HttpStatus.CREATED));
    }

    @Operation(
            summary = "UPDATE",
            description = "Update category (only admin can access)",
            tags = {"Category"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Category.class)
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
                                    examples = @ExampleObject(value = "{\"code\":404,\n\"message\":\"Category not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "409", description = "Conflict",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":409,\n\"message\":\"Data conflict occurred while trying to transact\"}")
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
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Category DTO",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DTO.CategoryDTO.class)
                    )
            ),
            parameters = {
                    @Parameter(name = "id", description = "Category ID", required = true, in = ParameterIn.PATH)
            },
            security = @SecurityRequirement(name = "Bearer token")
    )
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Category>> update(
            @PathVariable("id") Long id,
            @RequestBody DTO.CategoryDTO dto,
            ServerHttpRequest request
            ){
        return service.update(id, CategoryUtils.toEntity(dto), request)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "DELETE",
            description = "Delete category (only admin can access)",
            tags = {"Category"},
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
                                    examples = @ExampleObject(value = "{\"code\":404,\n\"message\":\"Category not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "409", description = "Conflict",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":409,\n\"message\":\"Data conflict occurred while trying to transact\"}")
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
                    @Parameter(name = "id", description = "Category ID", required = true, in = ParameterIn.PATH)
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
