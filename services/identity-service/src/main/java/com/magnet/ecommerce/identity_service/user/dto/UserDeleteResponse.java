package com.magnet.ecommerce.identity_service.user.dto;

import java.util.UUID;

public record UserDeleteResponse(
        String message,
        UUID userId
) {
}