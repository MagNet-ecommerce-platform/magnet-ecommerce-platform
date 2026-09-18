package com.magnet.ecommerce.identity_service.deactivation.controller;

import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.deactivation.dto.DeactivationRequest;
import com.magnet.ecommerce.identity_service.deactivation.service.DeactivationService;
import com.magnet.ecommerce.identity_service.exception.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Account Deactivation",
        description = "APIs for deactivating user accounts"
)
@SecurityRequirements
public class DeactivationController {

    private final DeactivationService deactivationService;

    @Operation(
            summary = "Deactivate user account",
            description = "Deactivates an active user account using the user's email address."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account deactivated successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = AuthenticationResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid deactivation request",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping("/deactivate")
    public ResponseEntity<AuthenticationResponse> deactivate(
            @Valid @RequestBody DeactivationRequest request) {

        AuthenticationResponse response =
                deactivationService.deactivate(request.email());

        return ResponseEntity.ok(response);
    }
}