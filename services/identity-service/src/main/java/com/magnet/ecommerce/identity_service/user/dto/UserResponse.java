package com.magnet.ecommerce.identity_service.user.dto;

import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        String firstName,
        String lastName,
        UserStatus status,
        boolean enabled,
        List<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
}