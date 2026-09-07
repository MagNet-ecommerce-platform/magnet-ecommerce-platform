package com.magnet.ecommerce.identity_service.repository;

import com.magnet.ecommerce.identity_service.entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ActivationTokenRepository
        extends JpaRepository<ActivationToken, UUID> {

    Optional<ActivationToken> findByToken(String token);

    boolean existsByToken(String token);
}