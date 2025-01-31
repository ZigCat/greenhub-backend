package com.github.zigcat.greenhub.user_provider.controllers;

import com.github.zigcat.greenhub.user_provider.entities.AppUser;
import com.github.zigcat.greenhub.user_provider.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            parameters = {
                    @Parameter(name = "id", description = "User ID", required = true, in = ParameterIn.DEFAULT)
            }
    )
    @GetMapping("/id")
    public Mono<ResponseEntity<AppUser>> getById(@RequestParam Long id){
        return service.retrieve(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
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
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public Flux<AppUser> getAll(){
        return service.list();
    }
}
