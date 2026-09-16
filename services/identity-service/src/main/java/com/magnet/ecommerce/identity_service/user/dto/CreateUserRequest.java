package com.magnet.ecommerce.identity_service.user.dto;

public record CreateUserRequest(
        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        String role
) {
}