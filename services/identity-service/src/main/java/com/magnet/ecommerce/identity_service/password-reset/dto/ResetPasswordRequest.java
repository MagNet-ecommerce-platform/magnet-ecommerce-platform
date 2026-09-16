package com.magnet.ecommerce.identity_service.passwordreset.dto;

public record ResetPasswordRequest(
        String token,
        String newPassword
) {
}