package com.magnet.ecommerce.identity_service.role.dto;

import java.util.List;

public record RolePermissionsResponse(
        String role,
        List<String> permissions
) {
}