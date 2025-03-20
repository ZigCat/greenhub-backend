package com.github.zigcat.greenhub.article_provider.presentation.controllers;

import com.github.zigcat.greenhub.article_provider.application.usecases.CategoryService;
import com.github.zigcat.greenhub.article_provider.domain.Article;
import com.github.zigcat.greenhub.article_provider.domain.Category;
import com.github.zigcat.greenhub.article_provider.presentation.DTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public/category")
@Tag(name = "PUBLIC", description = "Category's public endpoints")
public class CategoryPublicController {
    private final CategoryService service;

    public CategoryPublicController(CategoryService service) {
        this.service = service;
    }

    @Operation(
            summary = "GET BY ID",
            description = "Retrieving category by ID",
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
    public Mono<ResponseEntity<Category>> getById(
            @PathVariable("id") Long id
    ){
        return service.retrieve(id)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "GET ALL",
            description = "Listing categories",
            tags = {"Category"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Category.class)
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
            }
    )
    @GetMapping
    public Flux<Category> getAll(){
        return service.list();
    }
}
