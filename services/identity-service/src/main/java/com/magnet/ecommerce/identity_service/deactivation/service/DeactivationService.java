package com.magnet.ecommerce.identity_service.deactivation.service;

import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;
import com.magnet.ecommerce.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeactivationService {

    private final UserRepository userRepository;

    public AuthenticationResponse deactivate(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found"
                        )
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException(
                    "User account is not active"
            );
        }

        user.deactivate();

        userRepository.save(user);

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