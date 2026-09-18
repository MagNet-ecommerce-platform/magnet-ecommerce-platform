package com.magnet.ecommerce.identity_service.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUnlockRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email

) {
}