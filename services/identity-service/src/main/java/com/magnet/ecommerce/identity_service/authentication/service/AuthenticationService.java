package com.magnet.ecommerce.identity_service.authentication.service;

import com.magnet.ecommerce.identity_service.activation.service.ActivationTokenService;
import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginRequest;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.RegisterRequest;
import com.magnet.ecommerce.identity_service.entity.Role;
import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;
import com.magnet.ecommerce.identity_service.exception.InvalidRefreshTokenException;
import com.magnet.ecommerce.identity_service.repository.RoleRepository;
import com.magnet.ecommerce.identity_service.repository.UserRepository;
import com.magnet.ecommerce.identity_service.security.CustomUserDetails;
import com.magnet.ecommerce.identity_service.security.CustomUserDetailsService;
import com.magnet.ecommerce.identity_service.security.jwt.JwtService;
import com.magnet.ecommerce.identity_service.token.entity.RefreshToken;
import com.magnet.ecommerce.identity_service.token.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.magnet.ecommerce.identity_service.exception.AccountLockedException;
import com.magnet.ecommerce.identity_service.exception.InvalidLoginException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivationTokenService activationTokenService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already registered");
        }

        Instant now = Instant.now();

        User user = new User(
                UUID.randomUUID(),
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.firstName(),
                request.lastName(),
                UserStatus.PENDING,
                false,
                now,
                now
        );

        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Default CUSTOMER role is not configured"
                        )
                );

        user.addRole(customerRole);

        User savedUser = userRepository.save(user);

        activationTokenService.createToken(savedUser);

        return toAuthenticationResponse(savedUser);
    }

    @Transactional(
        noRollbackFor = {
                BadCredentialsException.class,
                InvalidLoginException.class,
                AccountLockedException.class
        }
)
public LoginResponse login(LoginRequest request) {

        /*
         * Find the user first so that we can:
         *
         * 1. Check whether the account is already locked.
         * 2. Increment failed login attempts when authentication fails.
         * 3. Reset failed login attempts after successful authentication.
         */
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        /*
         * Do not allow authentication attempts against an account
         * that has already been locked.
         */
        if (user.getStatus() == UserStatus.LOCKED) {
                throw new AccountLockedException(
                        "Account is locked. Please contact an administrator."
                );
            }

        Authentication authentication;

        try {

            authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.email(),
                                    request.password()
                            )
                    );

                } catch (BadCredentialsException exception) {

                        user.recordFailedLoginAttempt();
                    
                        int failedAttempts =
                                user.getFailedLoginAttempts();
                    
                        if (failedAttempts >= MAX_FAILED_LOGIN_ATTEMPTS) {
                    
                            user.lockAccount();
                    
                            userRepository.save(user);
                    
                            throw new AccountLockedException(
                                    "Account locked. Too many failed login attempts."
                            );
                        }
                    
                        userRepository.save(user);
                    
                        int attemptsRemaining =
                                MAX_FAILED_LOGIN_ATTEMPTS - failedAttempts;
                    
                        String attemptWord =
                                attemptsRemaining == 1
                                        ? "attempt"
                                        : "attempts";
                    
                        throw new InvalidLoginException(
                                "Invalid email or password. "
                                        + attemptsRemaining
                                        + " "
                                        + attemptWord
                                        + " remaining.",
                                failedAttempts,
                                attemptsRemaining
                        );
                    }

        /*
         * Authentication succeeded.
         *
         * Reset the consecutive failed-login counter.
         */
        if (user.getFailedLoginAttempts() > 0) {
            user.resetFailedLoginAttempts();
            userRepository.save(user);
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String accessToken =
                jwtService.generateAccessToken(userDetails);

        String refreshToken =
                jwtService.generateRefreshToken(userDetails);

        String tokenHash =
                jwtService.hashToken(refreshToken);

        Instant expiresAt =
                jwtService.extractExpiration(refreshToken)
                        .toInstant();

        RefreshToken refreshTokenEntity =
                new RefreshToken(
                        userDetails.getUser(),
                        tokenHash,
                        expiresAt,
                        Instant.now()
                );

        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                900
        );
    }

    @Transactional
    public LoginResponse refreshAccessToken(String refreshToken) {

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException(
                    "Invalid refresh token"
            );
        }

        String tokenHash =
                jwtService.hashToken(refreshToken);

        RefreshToken storedRefreshToken =
                refreshTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Invalid refresh token"
                                )
                        );

        if (storedRefreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException(
                    "Invalid refresh token"
            );
        }

        if (storedRefreshToken.getExpiresAt()
                .isBefore(Instant.now())) {

            throw new InvalidRefreshTokenException(
                    "Invalid or expired refresh token"
            );
        }

        String username =
                jwtService.extractUsername(refreshToken);

        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailsService
                        .loadUserByUsername(username);

        if (!jwtService.isTokenValid(
                refreshToken,
                userDetails
        )) {

            throw new InvalidRefreshTokenException(
                    "Invalid or expired refresh token"
            );
        }

        /*
         * All validation passed.
         * Now revoke the old refresh token.
         */
        storedRefreshToken.revoke();

        /*
         * Generate new access token.
         */
        String newAccessToken =
                jwtService.generateAccessToken(userDetails);

        /*
         * Generate new refresh token.
         */
        String newRefreshToken =
                jwtService.generateRefreshToken(userDetails);

        String newRefreshTokenHash =
                jwtService.hashToken(newRefreshToken);

        Instant newRefreshTokenExpiresAt =
                jwtService.extractExpiration(newRefreshToken)
                        .toInstant();

        RefreshToken newRefreshTokenEntity =
                new RefreshToken(
                        userDetails.getUser(),
                        newRefreshTokenHash,
                        newRefreshTokenExpiresAt,
                        Instant.now()
                );

        refreshTokenRepository.save(newRefreshTokenEntity);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                900
        );
    }

    @Transactional
    public void logout(String refreshToken) {

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new InvalidRefreshTokenException(
                    "Invalid refresh token"
            );
        }

        String tokenHash =
                jwtService.hashToken(refreshToken);

        RefreshToken storedRefreshToken =
                refreshTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new InvalidRefreshTokenException(
                                        "Invalid refresh token"
                                )
                        );

        if (storedRefreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException(
                    "Invalid refresh token"
            );
        }

        storedRefreshToken.revoke();

        refreshTokenRepository.save(storedRefreshToken);
    }

    private AuthenticationResponse toAuthenticationResponse(User user) {

        return new AuthenticationResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStatus()
        );
    }
}
