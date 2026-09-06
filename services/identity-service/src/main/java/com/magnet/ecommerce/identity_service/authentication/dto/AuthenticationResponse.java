package com.magnet.ecommerce.identity_service.authentication.dto;

import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;

import java.util.UUID;

public record AuthenticationResponse(

        UUID id,

        String username,

        String email,

        String firstName,

        String lastName,

        UserStatus status

) {
}