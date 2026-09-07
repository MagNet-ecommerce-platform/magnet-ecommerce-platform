package com.magnet.ecommerce.identity_service.activation.service;

import com.magnet.ecommerce.identity_service.entity.ActivationToken;
import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.repository.ActivationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional
public class ActivationTokenService {

    private static final Duration TOKEN_EXPIRATION = Duration.ofHours(24);

    private final ActivationTokenRepository activationTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public ActivationToken createToken(User user) {

        String token = generateSecureToken();

        Instant now = Instant.now();
        Instant expiresAt = now.plus(TOKEN_EXPIRATION);

        ActivationToken activationToken = new ActivationToken(
                UUID.randomUUID(),
                token,
                user,
                expiresAt,
                false,
                now
        );

        return activationTokenRepository.save(activationToken);
    }

    private String generateSecureToken() {

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}