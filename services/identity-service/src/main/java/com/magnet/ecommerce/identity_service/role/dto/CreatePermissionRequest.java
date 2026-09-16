package com.magnet.ecommerce.identity_service.role.dto;

public record CreatePermissionRequest(
        String name,
        String description
) {
}