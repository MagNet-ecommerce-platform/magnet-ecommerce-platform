package com.magnet.ecommerce.identity_service.activation.controller;

import com.magnet.ecommerce.identity_service.activation.dto.ActivationRequest;
import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.activation.service.ActivationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class ActivationController {

    private final ActivationService activationService;

    @PostMapping("/activate")
    public ResponseEntity<AuthenticationResponse> activate(
            @Valid @RequestBody ActivationRequest request) {

        AuthenticationResponse response =
                activationService.activate(request.token());

        return ResponseEntity.ok(response);
    }
}