package com.magnet.ecommerce.identity_service.entity;

import java.time.Instant;
import java.util.UUID;

import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private UUID id;

    private String username;

    private String email;

    private String passwordHash;

    private String firstName;

    private String lastName;

    private boolean enabled;

    private Instant createdAt;

    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserStatus status = UserStatus.ACTIVE; // Default to ACTIVE
}
