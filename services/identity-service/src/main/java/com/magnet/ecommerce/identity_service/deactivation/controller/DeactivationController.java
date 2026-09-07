package com.magnet.ecommerce.identity_service.deactivation.controller;

import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.deactivation.dto.DeactivationRequest;
import com.magnet.ecommerce.identity_service.deactivation.service.DeactivationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class DeactivationController {

    private final DeactivationService deactivationService;

    @PostMapping("/deactivate")
    public ResponseEntity<AuthenticationResponse> deactivate(
            @Valid @RequestBody DeactivationRequest request) {

        AuthenticationResponse response =
                deactivationService.deactivate(request.email());

        return ResponseEntity.ok(response);
    }
}