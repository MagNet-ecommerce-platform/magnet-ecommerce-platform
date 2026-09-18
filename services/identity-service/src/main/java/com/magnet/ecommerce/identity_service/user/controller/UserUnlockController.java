package com.magnet.ecommerce.identity_service.user.controller;

import com.magnet.ecommerce.identity_service.exception.ErrorResponse;
import com.magnet.ecommerce.identity_service.user.dto.UserUnlockRequest;
import com.magnet.ecommerce.identity_service.user.dto.UserUnlockResponse;
import com.magnet.ecommerce.identity_service.user.service.UserUnlockService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(
        name = "Account Security",
        description = "Administrative account security operations"
)
public class UserUnlockController {

    private final UserUnlockService userUnlockService;

    public UserUnlockController(UserUnlockService userUnlockService) {
        this.userUnlockService = userUnlockService;
    }

    @Operation(
            summary = "Unlock user account",
            description = "Unlocks a user account that was locked after too many "
                    + "failed login attempts. Requires USER_UPDATE permission."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Account unlocked successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid unlock request",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Insufficient permissions",
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
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Account is not currently locked",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping("/unlock")
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public ResponseEntity<UserUnlockResponse> unlockAccount(
            @Valid @RequestBody UserUnlockRequest request
    ) {
        UserUnlockResponse response =
                userUnlockService.unlockAccount(request.email());

        return ResponseEntity.ok(response);
    }
}
