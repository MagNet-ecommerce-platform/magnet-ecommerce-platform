package com.magnet.ecommerce.identity_service.passwordreset.service;

import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.passwordreset.dto.ForgotPasswordResponse;
import com.magnet.ecommerce.identity_service.passwordreset.dto.ResetPasswordRequest;
import com.magnet.ecommerce.identity_service.passwordreset.entity.PasswordResetToken;
import com.magnet.ecommerce.identity_service.repository.PasswordResetTokenRepository;
import com.magnet.ecommerce.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.magnet.ecommerce.identity_service.token.entity.RefreshToken;
import com.magnet.ecommerce.identity_service.token.repository.RefreshTokenRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int RESET_TOKEN_EXPIRATION_MINUTES = 15;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public ForgotPasswordResponse forgotPassword(String email) {

        String normalizedEmail = email == null
                ? ""
                : email.trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElse(null);

        /*
         * Do not reveal whether the email exists.
         */
        if (user == null) {
            return new ForgotPasswordResponse(
                    "Password reset request processed successfully",
                    null
            );
        }

        /*
         * Invalidate any existing unused reset tokens
         * for this user before creating a new one.
         */
        invalidateExistingTokens(user);

        /*
         * Generate a cryptographically secure random token.
         */
        String rawToken = generateResetToken();

        /*
         * Never store the raw token in the database.
         */
        String tokenHash = hashToken(rawToken);

        Instant now = Instant.now();

        PasswordResetToken resetToken = new PasswordResetToken(
                UUID.randomUUID(),
                user,
                tokenHash,
                now.plus(
                        RESET_TOKEN_EXPIRATION_MINUTES,
                        ChronoUnit.MINUTES
                ),
                false,
                now
        );

        passwordResetTokenRepository.save(resetToken);

        /*
         * LOCAL DEVELOPMENT ONLY:
         * Return the raw token so Postman can test the
         * complete reset-password flow without email.
         *
         * This must be removed when email delivery is implemented.
         */
        return new ForgotPasswordResponse(
                "Password reset request processed successfully",
                rawToken
        );
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        if (request == null
                || request.token() == null
                || request.token().isBlank()) {

            throw new IllegalArgumentException(
                    "Reset token is required"
            );
        }

        if (request.newPassword() == null
                || request.newPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "New password is required"
            );
        }

        String tokenHash = hashToken(request.token());

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid password reset token"
                                )
                        );

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException(
                    "Password reset token has already been used"
            );
        }

        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException(
                    "Password reset token has expired"
            );
        }

        User user = resetToken.getUser();

        user.setUpdatedAt(Instant.now());

        /*
         * Never store the raw password.
         */
        user.setPasswordHash(
                passwordEncoder.encode(request.newPassword())
        );

        List<RefreshToken> refreshTokens =
        refreshTokenRepository.findAllByUserId(user.getId());

for (RefreshToken refreshToken : refreshTokens) {
    refreshToken.revoke();
}

if (!refreshTokens.isEmpty()) {
    refreshTokenRepository.saveAll(refreshTokens);
}

        /*
         * Make the token one-time-use.
         */
        resetToken.markUsed();

        passwordResetTokenRepository.save(resetToken);
    }

    private void invalidateExistingTokens(User user) {

        List<PasswordResetToken> existingTokens =
                passwordResetTokenRepository
                        .findByUserAndUsedFalse(user);
    
        for (PasswordResetToken token : existingTokens) {
            token.markUsed();
        }
    
        if (!existingTokens.isEmpty()) {
            passwordResetTokenRepository.saveAll(existingTokens);
        }
    }

    private String generateResetToken() {

        byte[] tokenBytes = new byte[32];

        secureRandom.nextBytes(tokenBytes);

        return HexFormat.of().formatHex(tokenBytes);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    exception
            );
        }
    }
}