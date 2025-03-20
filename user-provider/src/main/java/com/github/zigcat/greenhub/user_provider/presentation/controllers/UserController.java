package com.github.zigcat.greenhub.user_provider.presentation.controllers;

import com.github.zigcat.greenhub.user_provider.presentation.utils.UserUtils;
import com.github.zigcat.greenhub.user_provider.domain.AppUser;
import com.github.zigcat.greenhub.user_provider.application.usecases.UserService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/protected")
@Tag(name = "PROTECTED", description = "User's protected endpoints")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(
            summary = "UPDATE",
            description = "Updating user's info by ID. It doesn't require all poles of UserDTO to be no-null, only necessary for your case",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AppUser.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":400,\n\"message\":\"Wrong param\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Not enough rights for this action\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"User not found\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal server error\"}")
                            ))
            },
            parameters = {
                    @Parameter(name = "id", description = "User ID", required = true, in = ParameterIn.PATH)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated user's fields",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PresentationDTO.UserDTO.class)
                    )
            ),
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
            summary = "DELETE",
            description = "Deleting user by ID provided in params",
            tags = {"User"},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Success",
                            content = @Content()
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Not enough rights for this action\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "409", description = "Conflict",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":403,\n\"message\":\"Data conflict occurred while trying to transact\"}")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PresentationDTO.ApiError.class),
                                    examples = @ExampleObject(value = "{\"code\":500,\n\"message\":\"Internal server error\"}")
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
