package com.magnet.ecommerce.identity_service.authentication.service;

import com.magnet.ecommerce.identity_service.activation.service.ActivationTokenService;
import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginRequest;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.RegisterRequest;
import com.magnet.ecommerce.identity_service.entity.Role;
import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;
import com.magnet.ecommerce.identity_service.repository.RoleRepository;
import com.magnet.ecommerce.identity_service.repository.UserRepository;
import com.magnet.ecommerce.identity_service.security.CustomUserDetails;
import com.magnet.ecommerce.identity_service.security.CustomUserDetailsService;
import com.magnet.ecommerce.identity_service.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.magnet.ecommerce.identity_service.exception.InvalidRefreshTokenException;
import java.time.Instant;
import java.util.UUID;
import com.magnet.ecommerce.identity_service.token.entity.RefreshToken;
import com.magnet.ecommerce.identity_service.token.repository.RefreshTokenRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

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

    @Transactional
    public LoginResponse login(LoginRequest request) {

    Authentication authentication =
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

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
}