package com.magnet.ecommerce.identity_service.role.dto;

public record CreateRoleRequest(
        String name,
        String description
) {
}