package com.magnet.ecommerce.identity_service.authentication.controller;

import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginRequest;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.RegisterRequest;
import com.magnet.ecommerce.identity_service.authentication.service.AuthenticationService;
import com.magnet.ecommerce.identity_service.authentication.dto.RefreshTokenRequest;
import com.magnet.ecommerce.identity_service.authentication.dto.LogoutRequest;
import com.magnet.ecommerce.identity_service.passwordreset.dto.ForgotPasswordRequest;
import com.magnet.ecommerce.identity_service.passwordreset.dto.ForgotPasswordResponse;
import com.magnet.ecommerce.identity_service.passwordreset.dto.ResetPasswordRequest;
import com.magnet.ecommerce.identity_service.passwordreset.service.PasswordResetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthenticationResponse response =
                authenticationService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response =
                authenticationService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshAccessToken(
        @Valid @RequestBody RefreshTokenRequest request) {

    LoginResponse response =
            authenticationService.refreshAccessToken(
                    request.refreshToken()
            );

    return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @Valid @RequestBody LogoutRequest request) {

    authenticationService.logout(
            request.refreshToken()
    );

    return ResponseEntity.noContent().build();
}
@PostMapping("/forgot-password")
public ResponseEntity<ForgotPasswordResponse> forgotPassword(
        @RequestBody ForgotPasswordRequest request) {

    ForgotPasswordResponse response =
            passwordResetService.forgotPassword(request.email());

    return ResponseEntity.ok(response);
}
@PostMapping("/reset-password")
public ResponseEntity<Map<String, String>> resetPassword(
        @RequestBody ResetPasswordRequest request) {

    passwordResetService.resetPassword(request);

    return ResponseEntity.ok(
            Map.of(
                    "message",
                    "Password reset successfully"
            )
    );
}
}