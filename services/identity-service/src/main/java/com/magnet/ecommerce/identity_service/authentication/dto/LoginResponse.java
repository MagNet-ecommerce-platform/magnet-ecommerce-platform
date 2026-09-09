package com.magnet.ecommerce.identity_service.authentication.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {
}