package com.magnet.ecommerce.identity_service.deactivation.dto;

import jakarta.validation.constraints.NotBlank;

public record DeactivationRequest(

        @NotBlank(message = "Email is required")
        String email

) {
}