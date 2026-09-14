package com.magnet.ecommerce.identity_service.role.dto;

public record PermissionAssignmentResponse(
        String message,
        String role,
        String permission
) {
}