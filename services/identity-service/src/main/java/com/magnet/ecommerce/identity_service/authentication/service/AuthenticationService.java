package com.magnet.ecommerce.identity_service.authentication.service;

import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.authentication.dto.LoginRequest;
import com.magnet.ecommerce.identity_service.authentication.dto.RegisterRequest;
import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;
import com.magnet.ecommerce.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.magnet.ecommerce.identity_service.activation.service.ActivationTokenService;
import com.magnet.ecommerce.identity_service.entity.Role;
import com.magnet.ecommerce.identity_service.repository.RoleRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivationTokenService activationTokenService;
    private final RoleRepository roleRepository;

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

    @Transactional(readOnly = true)
    public AuthenticationResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new BadCredentialsException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new IllegalStateException("User account is disabled");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("User account is not active");
        }

        return toAuthenticationResponse(user);
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