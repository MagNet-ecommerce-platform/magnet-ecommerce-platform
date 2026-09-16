package com.magnet.ecommerce.identity_service.passwordreset.dto;

public record ForgotPasswordResponse(
        String message,
        String resetToken
) {
}