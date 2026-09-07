package com.magnet.ecommerce.identity_service.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "activation_tokens",
        indexes = {
                @Index(
                        name = "idx_activation_tokens_token",
                        columnList = "token"
                ),
                @Index(
                        name = "idx_activation_tokens_user_id",
                        columnList = "user_id"
                )
        }
)
public class ActivationToken {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ActivationToken() {
        // Required by JPA
    }

    public ActivationToken(
            UUID id,
            String token,
            User user,
            Instant expiresAt,
            boolean used,
            Instant createdAt) {

        this.id = id;
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.used = used;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }
    
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    public void markUsed() {
        this.used = true;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}