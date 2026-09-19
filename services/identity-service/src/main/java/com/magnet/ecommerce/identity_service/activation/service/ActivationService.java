package com.magnet.ecommerce.identity_service.activation.service;

import com.magnet.ecommerce.identity_service.authentication.dto.AuthenticationResponse;
import com.magnet.ecommerce.identity_service.entity.ActivationToken;
import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;
import com.magnet.ecommerce.identity_service.repository.ActivationTokenRepository;
import com.magnet.ecommerce.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.magnet.ecommerce.identity_service.kafka.IdentityEventPublisher;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivationService {

    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;
    private final IdentityEventPublisher identityEventPublisher;
    public AuthenticationResponse activate(String token) {

        ActivationToken activationToken =
                activationTokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid activation token"
                                )
                        );

        if (activationToken.isUsed()) {
            throw new IllegalStateException(
                    "Activation token has already been used"
            );
        }

        if (activationToken.isExpired()) {
            throw new IllegalStateException(
                    "Activation token has expired"
            );
        }

        User user = activationToken.getUser();

        if (user.getStatus() != UserStatus.PENDING) {
            throw new IllegalStateException(
                    "User account is not pending activation"
            );
        }

        user.activate();

        activationToken.markUsed();

        userRepository.save(user);
        activationTokenRepository.save(activationToken);
        identityEventPublisher.publishUserActivated(user);

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