package com.magnet.ecommerce.identity_service.repository;

import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.passwordreset.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    List<PasswordResetToken> findByUserAndUsedFalse(User user);
}