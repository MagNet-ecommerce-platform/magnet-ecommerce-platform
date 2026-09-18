package com.magnet.ecommerce.identity_service.activation.controller;

import com.magnet.ecommerce.identity_service.activation.dto.ActivationRequest;
import com.magnet.ecommerce.identity_service.activation.service.ActivationService;
import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
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
        name = "Account Activation",
        description = "APIs for activating registered user accounts"
)
@SecurityRequirements
public class ActivationController {

    private final ActivationService activationService;

    @Operation(
            summary = "Activate user account",
            description = "Activates a pending user account using a valid activation token."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account activated successfully",
                    content = @Content(
                            schema = @Schema(
                                    implementation = AuthenticationResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired activation token",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Activation token not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping("/activate")
    public ResponseEntity<AuthenticationResponse> activate(
            @Valid @RequestBody ActivationRequest request) {

        AuthenticationResponse response =
                activationService.activate(request.token());

        return ResponseEntity.ok(response);
    }
}
