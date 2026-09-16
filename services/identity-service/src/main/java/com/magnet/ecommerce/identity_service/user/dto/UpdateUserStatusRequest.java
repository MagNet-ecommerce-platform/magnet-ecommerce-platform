package com.magnet.ecommerce.identity_service.user.dto;

import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;

public record UpdateUserStatusRequest(
        UserStatus status
) {
}