package com.github.zigcat.greenhub.user_provider.controllers;

import com.github.zigcat.greenhub.user_provider.dto.mq.requests.RegisterRequest;
import com.github.zigcat.greenhub.user_provider.dto.rest.messages.ApiError;
import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import com.github.zigcat.greenhub.user_provider.entities.Role;
import com.github.zigcat.greenhub.user_provider.exceptions.ForbiddenException;
import com.github.zigcat.greenhub.user_provider.exceptions.NotFoundException;
import com.github.zigcat.greenhub.user_provider.services.UserService;
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
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":404,\n\"message\":\"User not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
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
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new NotFoundException("User with this ID not found")));
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
                                    schema = @Schema(implementation = ApiError.class),
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
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Not enough rights for this action\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
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
            @RequestBody RegisterRequest updated,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Scopes") String scopes,
            @RequestHeader("X-Role") String role){
        return service.retrieve(id)
                .filter(user -> {
                    if(user.getEmail().equals(username)) return true;
                    if(scopes.contains("user.manage") || role.equals(Role.ADMIN.toString())) return true;
                    throw new ForbiddenException("Not enough rights for this action");
                })
                .flatMap(user -> service.update(updated, user)
                            .map(ResponseEntity::ok));
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
                                    schema = @Schema(implementation = ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Not enough rights for this action\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiError.class),
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
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-Scopes") String scopes,
            @RequestHeader("X-Role") String role
    ){
        return service.retrieve(id)
                .filter(user -> {
                    if(user.getEmail().equals(username)) return true;
                    if(scopes.contains("user.manage") || role.equals(Role.ADMIN.toString())) return true;
                    throw new ForbiddenException("Not enough rights for this action");
                })
                .flatMap(user ->
                        service.delete(id)
                            .then(Mono.just(ResponseEntity.noContent().build())));
    }
}
