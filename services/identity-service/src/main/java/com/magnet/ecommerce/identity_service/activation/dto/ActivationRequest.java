package com.magnet.ecommerce.identity_service.activation.dto;

import jakarta.validation.constraints.NotBlank;

public record ActivationRequest(

        @NotBlank(message = "Activation token is required")
        String token

) {
}