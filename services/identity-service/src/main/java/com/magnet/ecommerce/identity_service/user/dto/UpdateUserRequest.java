package com.magnet.ecommerce.identity_service.user.dto;

public record UpdateUserRequest(
        String username,
        String email,
        String firstName,
        String lastName
) {
}