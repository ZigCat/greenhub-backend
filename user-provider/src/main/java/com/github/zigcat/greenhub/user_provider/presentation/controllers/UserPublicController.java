package com.github.zigcat.greenhub.user_provider.presentation.controllers;

import com.github.zigcat.greenhub.user_provider.application.usecases.UserService;
import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.presentation.PresentationDTO;
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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/public")
@Tag(name = "PUBLIC", description = "User's public endpoints")
public class UserPublicController {
    private final UserService service;

    public UserPublicController(UserService service) {
        this.service = service;
    }

    @Operation(
            summary = "GET BY ID",
            description = "Getting user by ID provided in params",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = AppUser.class
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":404,\n\"message\":\"User not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal Server Error\"}")
                            ))
            },
            parameters = {
                    @Parameter(name = "id", description = "User ID", required = true, in = ParameterIn.PATH)
            }
    )
    @GetMapping("/{id}")
    public Mono<ResponseEntity<AppUser>> getById(@PathVariable("id") Long id){
        return service.retrieveByIdWithScopes(id)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "GET ALL",
            description = "Getting all users",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = AppUser.class)
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal Server Error\"}")
                            ))
            }
    )
    @GetMapping
    public Flux<AppUser> getAll(){
        return service.list();
    }

    @PostMapping
    public Flux<AppUser> getByIds(@RequestBody List<Long> ids){
        return service.listByIds(ids);
    }
}
