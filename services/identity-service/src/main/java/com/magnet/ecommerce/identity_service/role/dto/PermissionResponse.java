package com.magnet.ecommerce.identity_service.role.dto;

import java.util.UUID;

public record PermissionResponse(
        UUID id,
        String name,
        String description
) {
}