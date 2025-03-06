package com.github.zigcat.greenhub.user_provider.presentation.controllers;

import com.github.zigcat.greenhub.user_provider.presentation.utils.UserUtils;
import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.application.usecases.UserService;
import com.github.zigcat.greenhub.user_provider.presentation.PresentationDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "User", description = "User's CRUD and other processes related controller")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(
            summary = "Getting user by ID",
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
                    @ApiResponse(responseCode = "404", description = "Not Found",
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
        return service.retrieve(id)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Getting all users",
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

    @Operation(
            summary = "Updating user",
            description = "Updating user's info by ID. It doesn't require all poles of RegisterRequest to be no-null, only necessary for your case",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppUser.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Access Denied",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Not enough rights for this action\"}")
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
            },
            security = @SecurityRequirement(name = "Bearer token")
    )
    @PatchMapping("/{id}")
    public Mono<ResponseEntity<AppUser>> update(
            @PathVariable("id") Long id,
            @RequestBody PresentationDTO.UserDTO dto,
            ServerHttpRequest request){
        return service.update(id, UserUtils.toEntity(dto), request)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Deleting user",
            description = "Deleting user by ID provided in params",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Success",
                            content = @Content()
                    ),
                    @ApiResponse(responseCode = "403", description = "Access Denied",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Not enough rights for this action\"}")
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
