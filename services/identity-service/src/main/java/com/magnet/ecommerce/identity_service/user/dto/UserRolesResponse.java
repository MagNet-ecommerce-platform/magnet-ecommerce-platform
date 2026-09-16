package com.magnet.ecommerce.identity_service.user.dto;

import java.util.List;
import java.util.UUID;

public record UserRolesResponse(
        String message,
        UUID userId,
        String role,
        List<String> roles
) {
}