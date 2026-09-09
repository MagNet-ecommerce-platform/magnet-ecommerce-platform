package com.magnet.ecommerce.identity_service.token.entity;

import com.magnet.ecommerce.identity_service.entity.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(
                        name = "idx_refresh_tokens_token_hash",
                        columnList = "token_hash",
                        unique = true
                ),
                @Index(
                        name = "idx_refresh_tokens_user_id",
                        columnList = "user_id"
                )
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_refresh_tokens_user"
            )
    )
    private User user;

    @Column(
            name = "token_hash",
            nullable = false,
            unique = true,
            length = 64
    )
    private String tokenHash;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private Instant expiresAt;

    @Column(
            name = "revoked",
            nullable = false
    )
    private boolean revoked = false;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "revoked_at"
    )
    private Instant revokedAt;

    protected RefreshToken() {
        // Required by JPA
    }

    public RefreshToken(
            User user,
            String tokenHash,
            Instant expiresAt,
            Instant createdAt
    ) {
        this.user = user;
        this.tokenHash = tokenHash;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.revoked = false;
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void revoke() {
        this.revoked = true;
        this.revokedAt = Instant.now();
    }
}